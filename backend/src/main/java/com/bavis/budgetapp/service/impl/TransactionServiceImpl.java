package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.*;
import com.bavis.budgetapp.entity.*;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.filter.TransactionFilters;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * @author Kellen Bavis
 *
 * Implementation of our Transaction Service functionality
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final PlaidServiceImpl _plaidService;

    private final AccountServiceImpl _accountService;

    private final ConnectionServiceImpl _connectionService;

    private final UserServiceImpl _userService;

    private final TransactionRepository _transactionRepository;

    private final TransactionMapper _transactionMapper;

    private final TransactionFilters _transactionFilters;

    @Lazy
    private final CategoryServiceImpl categoryService;

    @Override
    public SyncTransactionsDto syncTransactions(AccountsDto accountsDto) throws PlaidServiceException{
        log.info("Syncing Transactions for the following Accounts: [{}]", accountsDto.getAccounts());

        List<Transaction> allModifiedOrAddedTransactions = new ArrayList<>();
        List<String> allRemovedTransactionIds = new ArrayList<>();
        List<Transaction> previousMonthTransactions = new ArrayList<>();

        Set<String> pendingTransactionIds = new HashSet<>();

        boolean hasMore;
        boolean updateOriginalCursor;
        String accessToken;
        String previousCursor;
        String originalCursor;
        Account account;
        Connection accountConnection;

        //Sync Transaction for each specified Account
        for(String accountId: accountsDto.getAccounts()){
            try{
                //Fetch relevant Account/Connection information
                account = _accountService.read(accountId);
                log.info("Syncing Transactions for Account ID {}", account.getAccountId());
                accountConnection = account.getConnection();
                originalCursor = accountConnection.getOriginalCursor();
                accessToken = accountConnection.getAccessToken();
                previousCursor = accountConnection.getPreviousCursor();
                hasMore = true;
                updateOriginalCursor = StringUtils.isBlank(originalCursor); //flag to determine if we need to persist originalCursor or not

                //Collect Added, Modified, and Removed Transactions for Account until Plaid specifies none remain
                while(hasMore){
                    PlaidTransactionSyncResponseDto syncResponseDto = _plaidService.syncTransactions(accessToken, previousCursor);
                    log.info("PlaidTransactionSyncResponseDto for Account ID {} : [{}]", accountId, syncResponseDto);

                    //Collect Added Transactions
                    allModifiedOrAddedTransactions.addAll(mapAddedTransactions(syncResponseDto.getAdded(), account, pendingTransactionIds));

                    //Collect Modified Transactions
                    allModifiedOrAddedTransactions.addAll(mapModifiedTransactions(syncResponseDto.getModified(), account));

                    //Account for Previous Months Transactions
                    List<PlaidTransactionDto> allPlaidTransactions = new ArrayList<>();
                    allPlaidTransactions.addAll(syncResponseDto.getModified());
                    allPlaidTransactions.addAll(syncResponseDto.getAdded());
                    previousMonthTransactions.addAll(mapPreviousMonthTransactions(allPlaidTransactions, account, previousMonthTransactions));

                    //Collect Removed TransactionIds
                    List<String> removedTransactionIds = Optional.ofNullable(syncResponseDto.getRemoved()).stream().flatMap(List::stream)
                            .map(PlaidTransactionDto::getTransaction_id)
                            .toList();
                    log.info("Removed Transaction IDs for Account {} : [{}]", accountId, removedTransactionIds);
                    allRemovedTransactionIds.addAll(removedTransactionIds);

                    //Update Previous Cursor For Subsequent Request
                    previousCursor = syncResponseDto.getNext_cursor();

                    //Update OriginalCursor value if this is the first paginated response for the current Account
                    if(StringUtils.isBlank(originalCursor)){
                        originalCursor = previousCursor;

                    }

                    //Determine if Plaid has more Transactions to sync for current Account
                    hasMore = syncResponseDto.isHas_more();
                }

                //Update persisted Connection
                updateConnection(accountConnection, originalCursor, previousCursor, updateOriginalCursor);

            } catch (PlaidServiceException plaidServiceException){
               log.error("PlaidServiceException occurred while syncing transactions via our TransactionService: [{}]", plaidServiceException.getMessage());
               throw plaidServiceException;
            }
            catch(RuntimeException e){
                log.error("An error occurred while Syncing Transactions: [{}]", e.getMessage());
                throw new RuntimeException(e);
            }
        }

        //Persist updates
        if(!previousMonthTransactions.isEmpty()) {
            // save previous month transactions
            _transactionRepository.saveAllAndFlush(previousMonthTransactions);

            // filter out previous month transactions from all added/modified
            Set<String> previousMonthTransactionIds = previousMonthTransactions.stream().map(Transaction::getTransactionId).collect(Collectors.toSet());
            allModifiedOrAddedTransactions = allModifiedOrAddedTransactions.stream()
                    .filter(t -> !previousMonthTransactionIds.contains(t.getTransactionId()))
                    .toList();
        }
        if(!allModifiedOrAddedTransactions.isEmpty()) _transactionRepository.saveAllAndFlush(allModifiedOrAddedTransactions);

        List<String> filteredTransactionIds = new ArrayList<>();
        if(!allRemovedTransactionIds.isEmpty()) {

            //filter out transaction ids that correspond to user modified transactions (Plaid will remove previously pending transactions that are now finalized, but we don't want the user to need to re-allocate/assign transactions each time)
            filteredTransactionIds = allRemovedTransactionIds.stream()
                    .filter(transactionId -> !pendingTransactionIds.contains(transactionId))
                    .toList();

            if(!filteredTransactionIds.isEmpty())  _transactionRepository.deleteAllById(filteredTransactionIds);
        }

        //Return DTO
        return SyncTransactionsDto.builder()
                .allModifiedOrAddedTransactions(allModifiedOrAddedTransactions)
                .removedTransactionIds(filteredTransactionIds)
                .previousMonthTransactions(previousMonthTransactions)
                .build();
    }

    @Override
    public List<Transaction> readAll(){
        log.info("Attempting to read all Transaction entities corresponding to authenticated user's added Accounts and the current month");
        User currentAuthUser = _userService.getCurrentAuthUser();
        LocalDate currentDate = LocalDate.now();
        List<Account> accounts = currentAuthUser.getAccounts().stream()
                .filter(account -> !account.isDeleted())
                .toList();
        List<Category> categories = currentAuthUser.getCategories();
        List<Transaction> allUserTransactions = new ArrayList<>(); //transactions to return

        //Validate User Has Accounts To Fetch Transactions For
        if(accounts.isEmpty() && categories == null) {
            return new ArrayList<>();
        }


        //Fetch All Transactions associated with User Accounts if user has added Accounts
        if(!accounts.isEmpty()) {
            List<String> accountIds = accounts.stream()
                    .map(Account::getAccountId)
                    .collect(Collectors.toList());

            log.debug("Reading transactions that are within the same year/date as {} and corresponding to following account IDs: {}", currentDate, accountIds);
            List<Transaction> accountTransactions = new ArrayList<>(_transactionRepository.findByAccountIdsAndCurrentMonth(accountIds, currentDate));
            allUserTransactions.addAll(accountTransactions);
        }

        //Fetch All Transactions associated with User Categories if user has added Categories
        if(categories != null){
            List<Long> userCategoryIds = categories.stream()
                    .map(Category::getCategoryId)
                    .toList();

            //Fetch All Transactions Corresponding to these Category IDs Where Account is set to Null
            log.debug("Reading transactions that within the same year/date as {} and corresponding to the following Category IDs: {}", currentDate, userCategoryIds);
            List<Transaction> userCreatedTransactions = _transactionRepository.findByCategoryIdsAndCurrentMonth(userCategoryIds, currentDate);
            allUserTransactions.addAll(userCreatedTransactions);
        }

        log.info("All Transactions corresponding to UserID {} : [{}]", currentAuthUser.getUserId(), allUserTransactions);
        return  allUserTransactions;
    }

    @Override
    public Transaction readById(String transactionId) throws RuntimeException{
        log.info("Attempting to read Transaction by the following ID: [{}]", transactionId);
        return _transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction with the following ID not found: " + transactionId));
    }

    @Override
    public List<Transaction> fetchCategoryTransactions(long categoryId) {
        return _transactionRepository.findByCategoryCategoryId(categoryId);
    }

    @Override
    public Transaction reduceTransactionAmount(String transactionId, TransactionDto transactionDto) throws  RuntimeException {
        log.info("Attempting to reduce the Transaction amount to {} for the following Transaction ID: {}", transactionDto.getUpdatedAmount(), transactionId);

        //Fetch Transaction
        Transaction transaction = readById(transactionId);

        //Ensure updated amount is less than original amount
        if(transaction.getAmount() <= transactionDto.getUpdatedAmount()){
            throw new RuntimeException("Invalid Transaction amount; The provided amount must be less than the original Transaction amount.");
        }

        //Update Amount & Flag to indicate Transaction was updated, and persist
        transaction.setAmount(transactionDto.getUpdatedAmount());
        transaction.setUpdatedByUser(true);
        return _transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionName(String transactionId, String transactionName) throws RuntimeException{
        log.info("Updating Transaction with ID {} to have the following transactionName: {}", transactionId, transactionName);

        //Fetch Transaction
        Transaction transaction = readById(transactionId);

        //Update Name/Flag & Persist
        transaction.setName(transactionName);
        transaction.setUpdatedByUser(true);
        return _transactionRepository.save(transaction);
    }



    @Override
    public Transaction assignCategory(AssignCategoryRequestDto assignCategoryRequestDto) throws RuntimeException{
        log.info("Attempting to assign the Category corresponding to ID {} to the Transaction corresponding to the ID {}", assignCategoryRequestDto.getCategoryId(), assignCategoryRequestDto.getTransactionId());

        //Fetch Category
        Category category = categoryService.read(Long.parseLong(assignCategoryRequestDto.getCategoryId()));

        //Fetch Transaction
        Transaction transaction = readById(assignCategoryRequestDto.getTransactionId());

        //Update & Persist Transaction (Updates Cascade to Category)
        transaction.setCategory(category);
        log.debug("Updating Transaction with ID {} to be assigned to Category [{}]", transaction.getTransactionId(), category);
        return _transactionRepository.save(transaction);
    }

    @Override
    public Transaction addTransaction(TransactionDto transactionDto) throws RuntimeException{
        log.info("Attempting to map the TransactionDto [{}] to a Transaction entity and persist the record.", transactionDto);

        //Update TransactionDTO to not be assigned to any Account/Category
        transactionDto.setAccount(null);
        transactionDto.setCategory(null);

        //Map to Transaction
        Transaction transaction = _transactionMapper.toEntity(transactionDto);
        transaction.setTransactionId(UUID.randomUUID().toString()); //set Transaction ID to random, unique ID
        log.info("Mapped Transaction entity: [{}]", transaction);

        //Persist & Return
        return _transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public List<Transaction> splitTransaction(String transactionId, SplitTransactionDto splitTransactionDto) throws RuntimeException{
        log.info("Attempting to split out Transaction with the ID {}", transactionId);

        //Fetch Original Transaction by ID
        Transaction originalTransaction = readById(transactionId);
        log.info("Original Transaction being split: [{}]", originalTransaction);

        //Update TransactionDto's with original Transaction properties
        List<TransactionDto> updatedTransactionDtos = Optional.ofNullable(splitTransactionDto.getSplitTransactions())
                .stream()
                .flatMap(List::stream)
                .peek(dto -> {
                    dto.setCategory(originalTransaction.getCategory());
                    dto.setDate(originalTransaction.getDate());
                    dto.setLogoUrl(originalTransaction.getLogoUrl());
                    dto.setAccount(originalTransaction.getAccount());
                }).toList();
        log.info("Updated Transaction Dtos to be mapped to Transaction Entities: [{}]", updatedTransactionDtos);

        //Atomic Integer for Incremental Suffixes
        AtomicInteger counter = new AtomicInteger(1);

        //Map TransactionDto's to Transaction entities
        List<Transaction> splitTransactions = updatedTransactionDtos.stream()
                .map(_transactionMapper::toEntity)
                .peek(transaction -> transaction.setTransactionId(transactionId + "_" + counter.getAndIncrement()))
                .toList();
        log.info("Transaction entities to be persisted: [{}]", splitTransactions);

        //Delete Original Transaction
        _transactionRepository.deleteById(transactionId);

        //Save All New Split out Transactions
        _transactionRepository.saveAllAndFlush(splitTransactions);

        //Return New Transactions
        return splitTransactions;
    }


    @Override
    public void removeAssignedCategory(String transactionId) throws RuntimeException{
        //Fetch
        Transaction transaction = readById(transactionId);

        //Update & Persist
        log.info("Removing Category associated with the following Transaction: [{}]", transaction);
        transaction.setCategory(null);
        _transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(String transactionId) throws RuntimeException {
        //Fetch Transaction, or Throw Exception if Not Found
        Transaction transaction = readById(transactionId);

        // soft delete the transaction
        transaction.setDeleted(true);
        _transactionRepository.save(transaction);
    }

    @Override
    public void removeAccountTransactions(String accountId) {
        _transactionRepository.deleteByAccountAccountId(accountId);
    }

    /**
     * Functionality to map added PlaidTransactions to Transaction entities.
     * All Transactions with a negative amount and are not within the
     * current month should be filtered out.
     *
     * @param addedPlaidTransactions
     *          - newly added PlaidTransactions
     * @param account
     *          - Account the Transactions correspond to
     * @return
     *          - Transaction entities to be persisted
     */
    private List<Transaction> mapAddedTransactions(List<PlaidTransactionDto> addedPlaidTransactions, Account account, Set<String> pendingTransactionIds) {
        List<Transaction> addedTransactionEntities = Optional.ofNullable(addedPlaidTransactions).stream().flatMap(List::stream)
                .filter(_transactionFilters.isPendingAndUserModified(pendingTransactionIds)) //filter out plaid transactions that have been modfiied by user
                .map(_transactionMapper::toEntity)
                .peek(transaction -> {
                    //TODO: Intelligently assign CategoryType & Category in future
                    transaction.setCategory(null);
                    transaction.setAccount(account);
                })
                .filter(_transactionFilters.addedTransactionFilters())
                .toList();

        log.info("Added Transaction entities for Account {} to be persisted: [{}]", account.getAccountId(), addedTransactionEntities);
        return addedTransactionEntities;
    }

    /**
     * Functionality to map modified PlaidTransactions to Transaction entities.
     * All Transactions that don't currently exist in database (due to being
     * either split or removed), exist but were modified by user (amount reduced
     * or name changed), have a negative amount, or are not within the current month
     * should be filtered out and not persisted.
     *
     * @param modifiedPlaidTransactions
     *          - newly modified PlaidTransactions
     * @param account
     *          - Account the Transactions correspond to
     * @return
     *          - Transaction entities to be persisted
     */
    private List<Transaction> mapModifiedTransactions(List<PlaidTransactionDto> modifiedPlaidTransactions, Account account) {
        List<Transaction> modifiedTransactionEntities = Optional.ofNullable(modifiedPlaidTransactions).stream().flatMap(List::stream)
                .map(_transactionMapper::toEntity)
                .filter(_transactionFilters.modifiedTransactionFilters())
                .peek(transaction -> {
                    Transaction persistedTransaction = readById(transaction.getTransactionId());
                    transaction.setCategory(persistedTransaction.getCategory());  // set category to modified transactions current category
                    transaction.setAccount(account);
                })
                .toList();

        log.info("Modified Transaction entities for Account {} to be persisted: [{}]", account.getAccountId(), modifiedTransactionEntities);
        return modifiedTransactionEntities;
    }


    /***
     * Map modified & added PlaidTransaction to Transaction entities.
     * This is being done so users can correctly allocate Transactions to respective Categories for previous month & re-run Budget Performance logic
     *
     * @param allModifiedAndAddedPlaidTransactions
     *         - all modified or added Plaid Transactions
     * @param account
     *         - account these Transactions are corresponding to
     * @return
     *          - Transaction entities to persist and return
     */
    private List<Transaction> mapPreviousMonthTransactions(List<PlaidTransactionDto> allModifiedAndAddedPlaidTransactions, Account account, List<Transaction> previousMonthTransactions) {
        List<Transaction> prevMonthTransactionEntities = Optional.ofNullable(allModifiedAndAddedPlaidTransactions).stream().flatMap(List::stream)
                .map(_transactionMapper::toEntity)
                .peek(transaction -> {
                    //TODO: Intelligently assign CategoryType & Category in future
                    transaction.setCategory(null);
                    transaction.setAccount(account);
                })
                .filter(_transactionFilters.prevMonthTransactionFilters(previousMonthTransactions))
                .toList();


        log.info("Previous month Transactions for Account {} to be persisted: [{}]", account.getAccountId(), prevMonthTransactionEntities);
        return prevMonthTransactionEntities;
    }


    /**
     * Functionality to update persisted a Connection cursor(s) & sync time
     *
     * @param connection
     *          - Connection to update
     * @param originalCursor
     *          - Cursor from first paginated syncTransactions response from PlaidAPI
     * @param previousCursor
     *          - Most recent cursor from paginated response from PlaidAPI
     * @param updateOriginalCursor
     *          - Flag to determine if we must persist original cursor or not
     */
    private void updateConnection(Connection connection, String originalCursor, String previousCursor, boolean updateOriginalCursor) {
        connection.setPreviousCursor(previousCursor);
        connection.setLastSyncTime(LocalDateTime.now());
        if(updateOriginalCursor){
            connection.setOriginalCursor(originalCursor);
        }
        _connectionService.update(connection, connection.getConnectionId());
    }

}
