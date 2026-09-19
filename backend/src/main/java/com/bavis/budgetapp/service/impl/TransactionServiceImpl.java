package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.clients.SuggestionEngineClient;
import com.bavis.budgetapp.constants.AccountType;
import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.constants.PlaidErrorCode;
import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.request.AccountsDto;
import com.bavis.budgetapp.dto.request.AssignCategoryRequestDto;
import com.bavis.budgetapp.dto.request.CategorySuggestionRequest;
import com.bavis.budgetapp.dto.request.PlaidAccountDto;
import com.bavis.budgetapp.dto.request.UpdateAccountDto;
import com.bavis.budgetapp.dto.request.PlaidTransactionDto;
import com.bavis.budgetapp.dto.request.SplitTransactionDto;
import com.bavis.budgetapp.dto.request.TransactionDto;
import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.dto.response.AccountSyncFailureDto;
import com.bavis.budgetapp.dto.response.FetchTransactionsDto;
import com.bavis.budgetapp.dto.response.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.dto.response.SyncTransactionsDto;
import com.bavis.budgetapp.dto.response.CategoryResponseDto;
import com.bavis.budgetapp.dto.response.TransactionMetadata;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.AccountVt;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.entity.CategoryVt;
import com.bavis.budgetapp.filter.TransactionFilters;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.dao.StagedVenmoPaymentRepository;
import com.bavis.budgetapp.dao.VenmoAutomationRepository;
import com.bavis.budgetapp.entity.StagedVenmoPayment;
import com.bavis.budgetapp.entity.VenmoAutomation;
import com.bavis.budgetapp.service.EffectivityService;
import com.bavis.budgetapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private final SuggestionEngineClient _suggestionEngineClient;

    private final EffectivityService _effectivityService;

    private final CategoryMapper _categoryMapper;

    @Lazy
    private final CategoryServiceImpl categoryService;

    private final StagedVenmoPaymentRepository _stagedVenmoPaymentRepository;

    private final VenmoAutomationRepository _venmoAutomationRepository;

    @Override
    public SyncTransactionsDto syncTransactions(AccountsDto accountsDto) throws PlaidServiceException{
        log.info("Syncing Transactions for the following Accounts: [{}]", accountsDto.getAccounts());

        List<Transaction> allModifiedOrAddedTransactions = new ArrayList<>();
        List<String> allRemovedTransactionIds = new ArrayList<>();
        List<Transaction> previousMonthTransactions = new ArrayList<>();
        List<AccountResponseDto> updatedAccounts = new ArrayList<>();

        Set<String> pendingTransactionIds = new HashSet<>();

        // Accounts that could not be synced (and why), and the results of those that were
        List<AccountSyncFailureDto> failedAccounts = new ArrayList<>();
        List<AccountSyncResult> successfulSyncs = new ArrayList<>();

        //Sync Transaction for each specified Account. A failure for one Account must never prevent the others from syncing.
        for(String accountId: accountsDto.getAccounts()){
            Account account = null;
            try{
                account = _accountService.findEntity(accountId, null);
                AccountSyncResult result = syncAccount(account);

                // only merge once the Account has been fully pulled from Plaid, so a failure never leaves partial data behind
                allModifiedOrAddedTransactions.addAll(result.modifiedOrAddedTransactions);
                previousMonthTransactions.addAll(result.previousMonthTransactions);
                allRemovedTransactionIds.addAll(result.removedTransactionIds);
                pendingTransactionIds.addAll(result.pendingTransactionIds);
                updatedAccounts.addAll(result.updatedAccounts);
                successfulSyncs.add(result);

            } catch (PlaidServiceException plaidServiceException){
                log.error("PlaidServiceException occurred while syncing Account [{}]: [{}]", accountId, plaidServiceException.getMessage());
                failedAccounts.add(handlePlaidSyncFailure(accountId, account, plaidServiceException));
            } catch(RuntimeException e){
                log.error("An error occurred while Syncing Transactions for Account [{}]: [{}]", accountId, e.getMessage());
                failedAccounts.add(AccountSyncFailureDto.builder()
                        .accountId(accountId)
                        .accountName(resolveAccountName(account))
                        .message("An unexpected error occurred while syncing this account. Please try again.")
                        .requiresReauth(false)
                        .build());
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

        // Advance each successfully synced Account's cursor only now that its Transactions have been persisted. Persisting the
        // cursor any earlier would mean a later failure could cause Transactions to be skipped on the next sync.
        successfulSyncs.forEach(result ->
                updateConnection(result.connection, result.originalCursor, result.previousCursor, result.updateOriginalCursor));


        Long userId = _userService.getUserIdByAccountIds(accountsDto.getAccounts());
        if (userId == null) {
            throw new RuntimeException("Unable to retrieve user id pertaining to the following account ids: " + accountsDto.getAccounts());
        }

        // Enrich any pending Venmo transactions from staged emails before running category prediction
        try {
            List<Transaction> syncBatch = new ArrayList<>();
            syncBatch.addAll(allModifiedOrAddedTransactions);
            syncBatch.addAll(previousMonthTransactions);

            int enrichedCount = enrichVenmoTransactions(userId, syncBatch);
            if (enrichedCount > 0) {
                log.info("Enriched {} Venmo transactions for user ID {} during Plaid sync", enrichedCount, userId);
            }
        } catch (Exception e) {
            log.error("Failed to enrich Venmo transactions during sync for user ID {}: {}", userId, e.getMessage());
        }

        // make predictions for categorizing each Transaction
        predictCategories(allModifiedOrAddedTransactions, userId);
        predictCategories(previousMonthTransactions, userId);

        //create DTO to respond with
        return SyncTransactionsDto.builder()
                .allModifiedOrAddedTransactions(allModifiedOrAddedTransactions)
                .removedTransactionIds(filteredTransactionIds)
                .previousMonthTransactions(previousMonthTransactions)
                .updatedAccounts(updatedAccounts)
                .failedAccounts(failedAccounts)
                .build();
    }

    /**
     * Pull every page of Transactions Plaid has for a single Account.
     *
     * Nothing Transaction/Connection related is persisted here; everything is returned so the caller can persist it
     * only if the entire Account synced successfully.
     *
     * @param account
     *          - Account to sync
     * @return
     *          - everything retrieved from Plaid for the Account
     * @throws PlaidServiceException
     *          - thrown if Plaid fails to sync any page for the Account
     */
    private AccountSyncResult syncAccount(Account account) throws PlaidServiceException {
        log.info("Syncing Transactions for Account ID {}", account.getAccountId());
        Connection accountConnection = account.getConnection();
        String originalCursor = accountConnection.getOriginalCursor();
        String accessToken = accountConnection.getAccessToken();
        String previousCursor = accountConnection.getPreviousCursor();
        boolean hasMore = true;
        boolean updateOriginalCursor = StringUtils.isBlank(originalCursor); //flag to determine if we need to persist originalCursor or not

        AccountSyncResult result = new AccountSyncResult(accountConnection);

        //Collect Added, Modified, and Removed Transactions for Account until Plaid specifies none remain
        while(hasMore){
            PlaidTransactionSyncResponseDto syncResponseDto = _plaidService.syncTransactions(accessToken, previousCursor);
            log.info("Received Plaid sync response for Account ID {}: {} added, {} modified, {} removed, hasMore={}", account.getAccountId(),
                    size(syncResponseDto.getAdded()), size(syncResponseDto.getModified()), size(syncResponseDto.getRemoved()), syncResponseDto.isHas_more());

            //Collect Added Transactions
            result.modifiedOrAddedTransactions.addAll(mapAddedTransactions(syncResponseDto.getAdded(), account, result.pendingTransactionIds));

            //Collect Modified Transactions
            result.modifiedOrAddedTransactions.addAll(mapModifiedTransactions(syncResponseDto.getModified(), account));

            //Account for Previous Months Transactions
            List<PlaidTransactionDto> allPlaidTransactions = new ArrayList<>();
            allPlaidTransactions.addAll(syncResponseDto.getModified());
            allPlaidTransactions.addAll(syncResponseDto.getAdded());
            result.previousMonthTransactions.addAll(mapPreviousMonthTransactions(allPlaidTransactions, account, result.previousMonthTransactions));

            //Collect Removed TransactionIds
            List<String> removedTransactionIds = Optional.ofNullable(syncResponseDto.getRemoved()).stream().flatMap(List::stream)
                    .map(PlaidTransactionDto::getTransaction_id)
                    .toList();
            log.info("Removed {} Transactions for Account {}", removedTransactionIds.size(), account.getAccountId());
            result.removedTransactionIds.addAll(removedTransactionIds);

            //Update Previous Cursor For Subsequent Request
            previousCursor = syncResponseDto.getNext_cursor();

            //Update OriginalCursor value if this is the first paginated response for the current Account
            if(StringUtils.isBlank(originalCursor)){
                originalCursor = previousCursor;
            }

            // Update relevant Account with up-to-date balance information
            AccountVt currentVt = account.getValidTimes() != null && !account.getValidTimes().isEmpty()
                    ? _effectivityService.getActiveVt(account.getValidTimes(), LocalDate.now()) : null;
            if(syncResponseDto.getAccounts() != null && !syncResponseDto.getAccounts().isEmpty()) {
                Double extractedBalance = syncResponseDto.getAccounts().stream()
                        .filter(plaidAccountDto -> account.getAccountId().equals(plaidAccountDto.getAccountId()))
                        .findFirst()
                        .map(PlaidAccountDto::getBalances)
                        .map(balances -> {
                            AccountType currentType = currentVt != null ? currentVt.getAccountType() : null;
                            if (currentType == AccountType.CREDIT ||
                                currentType == AccountType.LOAN ||
                                currentType == AccountType.INVESTMENT) {
                                return balances.getCurrent() != null ? balances.getCurrent() : balances.getAvailable();
                            }
                            return balances.getAvailable() != null ? balances.getAvailable() : balances.getCurrent();
                        })
                        .filter(Objects::nonNull)
                        .map(BigDecimal::doubleValue)
                        .orElse(null);

                if (extractedBalance != null) {
                    UpdateAccountDto updateDto = UpdateAccountDto.builder()
                            .accountId(account.getAccountId())
                            .balance(extractedBalance)
                            .build();
                    AccountResponseDto updatedDto = _accountService.update(updateDto);
                    result.updatedAccounts.add(updatedDto);
                }
            } else if (currentVt != null && currentVt.getAccountType() == AccountType.INVESTMENT) {
                // referesh balance via /accounts/balance/get for Investment Accounts
                try {
                    double freshBalance = _plaidService.retrieveBalance(account.getAccountId(), accessToken);
                    UpdateAccountDto updateDto = UpdateAccountDto.builder()
                            .accountId(account.getAccountId())
                            .balance(freshBalance)
                            .build();
                    AccountResponseDto updatedDto = _accountService.update(updateDto);
                    result.updatedAccounts.add(updatedDto);
                    log.info("Refreshed live balance for Investment account {}: ${}", account.getAccountId(), freshBalance);
                } catch (Exception ex) {
                    log.warn("Could not retrieve live balance for investment account {}: {}", account.getAccountId(), ex.getMessage());
                }
            }

            //Determine if Plaid has more Transactions to sync for current Account
            hasMore = syncResponseDto.isHas_more();
        }

        result.originalCursor = originalCursor;
        result.previousCursor = previousCursor;
        result.updateOriginalCursor = updateOriginalCursor;
        return result;
    }

    /**
     * Build the failure we report back to the User for an Account that Plaid was unable to sync and, if Plaid says the
     * User must log in to their financial institution again, flag the Account's Connection so that we keep telling them
     * until it is fixed.
     */
    private AccountSyncFailureDto handlePlaidSyncFailure(String accountId, Account account, PlaidServiceException exception) {
        String errorCode = exception.getErrorCode();
        boolean requiresReauth = PlaidErrorCode.ITEM_LOGIN_REQUIRED.equals(errorCode);

        String message;
        if (requiresReauth) {
            message = "Your bank needs you to log in again before this account can be synced. Choose Reconnect to log in and restore access.";
            if (account != null && account.getConnection() != null) {
                try {
                    _connectionService.markDisconnected(account.getConnection().getConnectionId(), errorCode, exception.getPlaidMessage());
                } catch (RuntimeException e) {
                    // never let bookkeeping prevent us from reporting the failure (or from syncing the other Accounts)
                    log.error("Unable to flag Connection for Account [{}] as requiring re-authentication: [{}]", accountId, e.getMessage());
                }
            }
        } else {
            message = "Plaid was unable to sync this account: "
                    + (StringUtils.isNotBlank(exception.getPlaidMessage()) ? exception.getPlaidMessage() : "unknown error");
        }

        return AccountSyncFailureDto.builder()
                .accountId(accountId)
                .accountName(resolveAccountName(account))
                .errorCode(errorCode)
                .message(message)
                .requiresReauth(requiresReauth)
                .build();
    }

    /**
     * Resolve the display name of an Account, or null if it cannot be determined
     */
    private String resolveAccountName(Account account) {
        if (account == null || account.getValidTimes() == null || account.getValidTimes().isEmpty()) {
            return null;
        }
        AccountVt activeVt = _effectivityService.getActiveVt(account.getValidTimes(), LocalDate.now());
        return activeVt != null ? activeVt.getAccountName() : null;
    }

    @Override
    public FetchTransactionsDto getAll(LocalDate asOf) {
        log.info(
                "Attempting to read all Transaction entities corresponding to authenticated user's added Accounts and asOf [{}]",
                asOf);
        User currentAuthUser = _userService.getCurrentAuthUser();
        LocalDate currentDate = asOf != null ? asOf : LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        List<AccountResponseDto> accounts = _accountService.getAll(currentDate);
        List<Category> categories = categoryService.findAllEntities(currentDate);

        // lists to seperate current month transactions vs prev month transactions that
        // are unassinged
        List<Transaction> currentMonthTransactions = new ArrayList<>();
        List<Transaction> unassignedPreviousMonthTransactions = new ArrayList<>();

        //Validate User Has Accounts To Fetch Transactions For
        if(accounts.isEmpty() && categories == null) {
            return FetchTransactionsDto.builder()
                    .currentMonthTransactions(currentMonthTransactions)
                    .unassignedPreviousMonthTransactions(unassignedPreviousMonthTransactions)
                    .build();
        }

        // Fetch All Transactions associated with User Accounts if user has added
        // Accounts
        if (!accounts.isEmpty()) {
            List<String> accountIds = accounts.stream()
                    .map(AccountResponseDto::getAccountId)
                    .collect(Collectors.toList());

            log.debug("Reading transactions that are within the same year/date as {} and corresponding to following account IDs: {}", currentDate, accountIds);
            List<Transaction> accountTransactions = _transactionRepository.findByAccountIdsAndCurrentMonthOrUnassignedPreviousMonth(accountIds, currentDate);

            // split into current-month vs unassigned previous-month based on transaction
            // date
            for (Transaction t : accountTransactions) {
                if (t.getDate() != null
                        && t.getDate().getMonthValue() == currentMonth
                        && t.getDate().getYear() == currentYear) {
                    currentMonthTransactions.add(t);
                } else {
                    unassignedPreviousMonthTransactions.add(t);
                }
            }
        }

        // Fetch All Transactions associated with User Categories if user has added
        // Categories (always current month)
        if (categories != null) {
            List<Long> userCategoryIds = categories.stream()
                    .map(Category::getCategoryId)
                    .toList();

            // Fetch All Transactions Corresponding to these Category IDs Where Account is
            // set to Null
            log.debug(
                    "Reading transactions that within the same year/date as {} and corresponding to the following Category IDs: {}",
                    currentDate, userCategoryIds);
            List<Transaction> userCreatedTransactions = _transactionRepository
                    .findByCategoryIdsAndCurrentMonth(userCategoryIds, currentDate);
            currentMonthTransactions.addAll(userCreatedTransactions);
        }

        // Populate suggestedCategoryDto for all fetched transactions
        populateSuggestedCategoryDtos(currentMonthTransactions);
        populateSuggestedCategoryDtos(unassignedPreviousMonthTransactions);

        log.info("Fetched {} current-month and {} unassigned previous-month transactions for UserID {}", currentMonthTransactions.size(), unassignedPreviousMonthTransactions.size(), currentAuthUser.getUserId());
        return FetchTransactionsDto.builder()
                .currentMonthTransactions(currentMonthTransactions)
                .unassignedPreviousMonthTransactions(unassignedPreviousMonthTransactions)
                .build();
    }

    @Override
    public Transaction findEntity(String transactionId) throws RuntimeException {
        return findOwnedTransaction(transactionId, false);
    }

    @Override
    public Transaction findEntityAllowingUnowned(String transactionId) throws RuntimeException {
        return findOwnedTransaction(transactionId, true);
    }

    /**
     * Fetch a Transaction and verify that the currently authenticated User owns it.
     *
     * A Transaction is owned by the User of its Account (Plaid Transactions) or, for manually created
     * Transactions that have no Account, by the User of its Category. A manually created Transaction that has
     * neither yet (i.e. it has just been created and not yet assigned a Category) has no owner at all.
     *
     * @param transactionId
     *          - ID of the Transaction to fetch
     * @param allowUnowned
     *          - whether a Transaction without any owner may be returned (needed to assign a new manual Transaction to a Category)
     * @return
     *          - the Transaction, if the authenticated User is permitted to access it
     */
    private Transaction findOwnedTransaction(String transactionId, boolean allowUnowned) {
        log.info("Attempting to read Transaction by the following ID: [{}]", transactionId);
        Transaction transaction = _transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction with the following ID not found: " + transactionId));

        User owner = null;
        if (transaction.getAccount() != null) {
            owner = transaction.getAccount().getUser();
        } else if (transaction.getCategory() != null) {
            owner = transaction.getCategory().getUser();
        }

        boolean permitted = (owner == null)
                ? allowUnowned && transaction.getAccount() == null && transaction.getCategory() == null
                : _userService.isCurrentAuthUser(owner.getUserId());

        // respond exactly as if the Transaction did not exist so IDs cannot be probed
        if (!permitted) {
            log.warn("Authenticated user attempted to access a Transaction [{}] that they do not own", transactionId);
            throw new RuntimeException("Transaction with the following ID not found: " + transactionId);
        }
        return transaction;
    }

    @Override
    public List<Transaction> fetchCategoryTransactions(long categoryId, LocalDate asOf) {
        LocalDate targetDate = (asOf != null) ? asOf : LocalDate.now();
        return _transactionRepository.findByCategoryCategoryIdAndAsOf(categoryId, targetDate);
    }

    @Override
    public List<Transaction> fetchCategoryTransactions(long categoryId) {
        return fetchCategoryTransactions(categoryId, LocalDate.now());
    }

    @Override
    public Transaction reduceTransactionAmount(String transactionId, TransactionDto transactionDto)
            throws RuntimeException {
        log.info("Attempting to reduce the Transaction amount to {} for the following Transaction ID: {}",
                transactionDto.getUpdatedAmount(), transactionId);

        // Fetch Transaction
        Transaction transaction = findEntity(transactionId);

        // Ensure updated amount is less than original amount
        if (transaction.getAmount() <= transactionDto.getUpdatedAmount()) {
            throw new RuntimeException(
                    "Invalid Transaction amount; The provided amount must be less than the original Transaction amount.");
        }

        //Update Amount & Flag to indicate Transaction was updated, and persist
        transaction.setAmount(transactionDto.getUpdatedAmount());
        transaction.setUpdatedByUser(true);
        return _transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionName(String transactionId, String transactionName) throws RuntimeException{
        log.info("Updating Transaction with ID {} to have the following transactionName: {}", transactionId, transactionName);

        // Fetch Transaction
        Transaction transaction = findEntity(transactionId);

        //Update Name/Flag & Persist
        transaction.setName(transactionName);
        transaction.setUpdatedByUser(true);
        return _transactionRepository.save(transaction);
    }



    @Override
    public Transaction assignCategory(AssignCategoryRequestDto assignCategoryRequestDto) throws RuntimeException{
        log.info("Attempting to assign the Category corresponding to ID {} to the Transaction corresponding to the ID {}", assignCategoryRequestDto.getCategoryId(), assignCategoryRequestDto.getTransactionId());

        // Fetch Category
        Category category = categoryService.findEntity(Long.parseLong(assignCategoryRequestDto.getCategoryId()), null);

        // Fetch Transaction
        Transaction transaction = findEntityAllowingUnowned(assignCategoryRequestDto.getTransactionId());

        //Update & Persist Transaction (Updates Cascade to Category)
        transaction.setCategory(category);
        log.debug("Updating Transaction with ID {} to be assigned to Category [{}]", transaction.getTransactionId(), category);
        return _transactionRepository.save(transaction);
    }

    @Override
    public Transaction addTransaction(TransactionDto transactionDto) throws RuntimeException{
        log.info("Attempting to map a TransactionDto to a Transaction entity and persist the record.");

        //Update TransactionDTO to not be assigned to any Account/Category
        transactionDto.setAccount(null);
        transactionDto.setCategory(null);

        //Map to Transaction
        Transaction transaction = _transactionMapper.toEntity(transactionDto);
        transaction.setTransactionId(UUID.randomUUID().toString()); //set Transaction ID to random, unique ID
        log.info("Mapped Transaction entity with ID [{}]", transaction.getTransactionId());

        //Persist & Return
        return _transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public List<Transaction> splitTransaction(String transactionId, SplitTransactionDto splitTransactionDto) throws RuntimeException{
        log.info("Attempting to split out Transaction with the ID {}", transactionId);

        // Fetch Original Transaction by ID
        Transaction originalTransaction = findEntity(transactionId);
        log.info("Splitting Transaction with ID [{}]", originalTransaction.getTransactionId());

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
        log.info("Mapping {} split TransactionDtos to Transaction Entities", updatedTransactionDtos.size());

        //Atomic Integer for Incremental Suffixes
        AtomicInteger counter = new AtomicInteger(1);

        //Map TransactionDto's to Transaction entities
        List<Transaction> splitTransactions = updatedTransactionDtos.stream()
                .map(_transactionMapper::toEntity)
                .peek(transaction -> transaction.setTransactionId(transactionId + "_" + counter.getAndIncrement()))
                .toList();
        log.info("Persisting {} split Transaction entities", splitTransactions.size());

        //Delete Original Transaction
        _transactionRepository.deleteById(transactionId);

        //Save All New Split out Transactions
        _transactionRepository.saveAllAndFlush(splitTransactions);

        //Return New Transactions
        return splitTransactions;
    }


    @Override
    public void removeAssignedCategory(String transactionId) throws RuntimeException {
        // Fetch
        Transaction transaction = findEntity(transactionId);

        //Update & Persist
        log.info("Removing Category associated with Transaction with ID [{}]", transaction.getTransactionId());
        transaction.setCategory(null);
        _transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(String transactionId) throws RuntimeException {
        // Fetch Transaction, or Throw Exception if Not Found
        Transaction transaction = findEntity(transactionId);

        // soft delete the transaction by setting endDate to yesterday
        transaction.setEndDate(LocalDate.now().minusDays(1));
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
    private List<Transaction> mapAddedTransactions(List<PlaidTransactionDto> addedPlaidTransactions, Account account,
            Set<String> pendingTransactionIds) {
        List<Transaction> addedTransactionEntities = Optional.ofNullable(addedPlaidTransactions).stream()
                .flatMap(List::stream)
                .filter(_transactionFilters.isPendingAndUserModified(pendingTransactionIds)) // filter out plaid
                                                                                             // transactions that have
                                                                                             // been modfiied by user
                .map(_transactionMapper::toEntity)
                .peek(transaction -> {
                    //TODO: Intelligently assign CategoryType & Category in future
                    transaction.setCategory(null);
                    transaction.setAccount(account);
                })
                .filter(_transactionFilters.addedTransactionFilters())
                .toList();

        log.info("Persisting {} added Transaction entities for Account {}", addedTransactionEntities.size(), account.getAccountId());
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
                    Transaction persistedTransaction = findEntity(transaction.getTransactionId());
                    transaction.setCategory(persistedTransaction.getCategory()); // set category to modified
                                                                                 // transactions current category
                    transaction.setAccount(account);
                })
                .toList();

        log.info("Persisting {} modified Transaction entities for Account {}", modifiedTransactionEntities.size(), account.getAccountId());
        return modifiedTransactionEntities;
    }


    /***
     * Map modified & added PlaidTransaction to Transaction entities.
     * This is being done so users can correctly allocate Transactions to respective
     * Categories for previous month & re-run Budget Performance logic
     *
     * @param allModifiedAndAddedPlaidTransactions
     *                                             - all modified or added Plaid
     *                                             Transactions
     * @param account
     *                                             - account these Transactions are
     *                                             corresponding to
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


        log.info("Persisting {} previous month Transactions for Account {}", prevMonthTransactionEntities.size(), account.getAccountId());
        return prevMonthTransactionEntities;
    }


    /**
     * Functionality to update persisted a Connection cursor(s) & sync time
     *
     * @param connection
     *          - Connection to update
     * @param originalCursor
     *                             - Cursor from first paginated syncTransactions
     *                             response from PlaidAPI
     * @param previousCursor
     *                             - Most recent cursor from paginated response from
     *                             PlaidAPI
     * @param updateOriginalCursor
     *                             - Flag to determine if we must persist original
     *                             cursor or not
     */
    private void updateConnection(Connection connection, String originalCursor, String previousCursor, boolean updateOriginalCursor) {
        connection.setPreviousCursor(previousCursor);
        connection.setLastSyncTime(LocalDateTime.now());
        // a successful sync proves the Connection is healthy, so clear any previously reported "needs attention" state
        connection.setConnectionStatus(ConnectionStatus.CONNECTED);
        connection.setErrorCode(null);
        connection.setErrorMessage(null);
        if(updateOriginalCursor){
            connection.setOriginalCursor(originalCursor);
        }
        _connectionService.update(connection, connection.getConnectionId());
    }


    public void predictCategories(List<Transaction> transactionsToPredict, Long userId) {

        for (Transaction transaction : transactionsToPredict) {

            // generate request
            TransactionMetadata metadata = TransactionMetadata.builder()
                    .amount(transaction.getAmount())
                    .dateTime(transaction.getDateTime())
                    .plaidDetailedCategory(transaction.getPersonalFinanceCategory().getDetailedCategory())
                    .plaidPrimaryCategory(transaction.getPersonalFinanceCategory().getPrimaryCategory())
                    .merchant(transaction.getMerchantName())
                    .build();
            CategorySuggestionRequest request = CategorySuggestionRequest.builder()
                    .userId(userId)
                    .transactionMetadata(metadata)
                    .build();


            try {
                Long categoryId = _suggestionEngineClient.predictCategory(request);

                // check if prediction was made & assign
                if (categoryId != null) {
                    Category suggestedCategory = categoryService.findEntity(categoryId, null);
                    transaction.setSuggestedCategory(suggestedCategory);

                    // Build CategoryResponseDto via EffectivityService + CategoryMapper
                    CategoryVt activeVt = _effectivityService.getActiveVt(suggestedCategory.getValidTimes(), LocalDate.now());
                    CategoryResponseDto dto = _categoryMapper.toResponseDto(suggestedCategory, activeVt);
                    transaction.setSuggestedCategoryDto(dto);
                } else {
                    log.info("No Category suggestion for Transaction {}", metadata);
                }
            } catch (Exception e) {
                log.error("An exception occurred while attempting to predict the Category ID for the transaction {} : {}", metadata, e.getMessage());
            }


        }
    }

    /**
     * Populates the @Transient suggestedCategoryDto field for transactions loaded from the DB.
     * Uses EffectivityService + CategoryMapper to resolve the active VT snapshot.
     *
     * @param transactions
     *          - List of transactions to populate suggestedCategoryDto for
     */
    private void populateSuggestedCategoryDtos(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            Category suggested = transaction.getSuggestedCategory();
            if (suggested != null && suggested.getValidTimes() != null && !suggested.getValidTimes().isEmpty()) {
                try {
                    CategoryVt activeVt = _effectivityService.getActiveVt(suggested.getValidTimes(), LocalDate.now());
                    transaction.setSuggestedCategoryDto(_categoryMapper.toResponseDto(suggested, activeVt));
                } catch (Exception e) {
                    log.warn("Unable to resolve suggestedCategoryDto for transaction {}: {}", transaction.getTransactionId(), e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional
    public int enrichVenmoTransactions(Long userId, List<Transaction> syncBatch) {
        if (syncBatch == null || syncBatch.isEmpty()) {
            return 0;
        }

        // Only consider unmatched staged Venmo payments from the start of the previous month onward
        LocalDateTime startOfPreviousMonth = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();

        List<StagedVenmoPayment> pendingPayments = _stagedVenmoPaymentRepository
                .findByUserUserIdAndMatchedIsFalseAndEmailTimestampGreaterThanEqualOrderByEmailTimestampAsc(userId, startOfPreviousMonth);

        if (pendingPayments.isEmpty()) {
            return 0;
        }

        log.info("Attempting to enrich Venmo transactions for user ID: {} (syncBatch size: {}, staged count: {})",
                userId, syncBatch.size(), pendingPayments.size());

        int enrichedCount = 0;
        double amountTolerance = 0.02;

        // Keep track of transaction IDs matched within this batch to prevent duplicate assignments
        Set<String> matchedBatchTxIds = new HashSet<>();

        for (StagedVenmoPayment staged : pendingPayments) {
            LocalDate emailDate = staged.getEmailTimestamp() != null 
                    ? staged.getEmailTimestamp().toLocalDate() 
                    : LocalDate.now();

            // Match strictly by amount & Venmo merchant/name, sorted by date proximity to staged email date
            List<Transaction> candidates = syncBatch.stream()
                    .filter(t -> t.getTransactionId() != null && !matchedBatchTxIds.contains(t.getTransactionId()))
                    .filter(t -> (t.getMerchantName() != null && t.getMerchantName().equalsIgnoreCase("venmo"))
                            || (t.getName() != null && t.getName().equalsIgnoreCase("venmo")))
                    .filter(t -> Math.abs(t.getAmount() - staged.getAmount()) <= amountTolerance)
                    .sorted((t1, t2) -> {
                        if (t1.getDate() == null && t2.getDate() == null) return 0;
                        if (t1.getDate() == null) return 1;
                        if (t2.getDate() == null) return -1;
                        long d1 = Math.abs(ChronoUnit.DAYS.between(emailDate, t1.getDate()));
                        long d2 = Math.abs(ChronoUnit.DAYS.between(emailDate, t2.getDate()));
                        return Long.compare(d1, d2);
                    })
                    .toList();

            if (candidates.isEmpty()) {
                log.debug("No matching Venmo candidate found in syncBatch for staged payment ID {} (${})",
                        staged.getStagedId(), staged.getAmount());
                continue;
            }

            Transaction bestMatch = candidates.get(0);
            matchedBatchTxIds.add(bestMatch.getTransactionId());

            bestMatch.setName(staged.getEnrichedName());
            _transactionRepository.save(bestMatch);

            staged.setMatched(true);
            staged.setMatchedAt(LocalDateTime.now());
            _stagedVenmoPaymentRepository.save(staged);

            enrichedCount++;
            log.info("Enriched transaction ID {} on date {} with name '{}' (Staged ID: {}, Email Date: {})",
                    bestMatch.getTransactionId(), bestMatch.getDate(), staged.getEnrichedName(), staged.getStagedId(), emailDate);
        }

        if (enrichedCount > 0) {
            Optional<VenmoAutomation> automationOpt = _venmoAutomationRepository.findByUserUserId(userId);
            if (automationOpt.isPresent()) {
                VenmoAutomation automation = automationOpt.get();
                automation.setLastProcessedAt(LocalDateTime.now());
                automation.setEnrichedCount(automation.getEnrichedCount() + enrichedCount);
                _venmoAutomationRepository.save(automation);
            }
        }

        return enrichedCount;
    }

    /**
     * Null-safe size helper so we can log counts of Plaid results instead of their (sensitive) contents
     */
    private static int size(List<?> list) {
        return list == null ? 0 : list.size();
    }

    /**
     * Everything retrieved from Plaid for a single Account during a sync; only persisted once the whole Account succeeded
     */
    private static final class AccountSyncResult {
        private final Connection connection;
        private final List<Transaction> modifiedOrAddedTransactions = new ArrayList<>();
        private final List<Transaction> previousMonthTransactions = new ArrayList<>();
        private final List<String> removedTransactionIds = new ArrayList<>();
        private final Set<String> pendingTransactionIds = new HashSet<>();
        private final List<AccountResponseDto> updatedAccounts = new ArrayList<>();
        private String originalCursor;
        private String previousCursor;
        private boolean updateOriginalCursor;

        private AccountSyncResult(Connection connection) {
            this.connection = connection;
        }
    }
}
