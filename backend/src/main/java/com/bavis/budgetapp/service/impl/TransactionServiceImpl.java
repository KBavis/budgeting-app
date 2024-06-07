package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.*;
import com.bavis.budgetapp.entity.*;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Kellen Bavis
 *
 * Implementation of our Transaction Service functionality
 */
@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {

    private PlaidServiceImpl _plaidService;

    private AccountServiceImpl _accountService;

    private ConnectionServiceImpl _connectionService;

    private UserServiceImpl _userService;

    private TransactionRepository _transactionRepository;

    private TransactionMapper _transactionMapper;

    private CategoryServiceImpl categoryService;



    public TransactionServiceImpl(PlaidServiceImpl plaidService, AccountServiceImpl accountService, TransactionMapper transactionMapper, TransactionRepository transactionRepository, ConnectionServiceImpl connectionService, UserServiceImpl userService, CategoryServiceImpl categoryService) {
        this._transactionRepository = transactionRepository;
        this._plaidService = plaidService;
        this._userService = userService;
        this._accountService = accountService;
        this._transactionMapper = transactionMapper;
        this._connectionService = connectionService;
        this.categoryService = categoryService;
    }

    //TODO: considering returning separate DTO w/ modified/added/removed lists so we can update frontend state with transactions that must be removed
    @Override
    public List<Transaction> syncTransactions(TransactionSyncRequestDto transactionSyncRequestDto) throws PlaidServiceException{
        List<Transaction> allModifiedOrAddedTransactions = new ArrayList<>();

        //Sync Transaction for each specified Account
        for(String accountId: transactionSyncRequestDto.getAccounts()){
            try{
                //Extract Access Token and Previous Cursor for Specified Account
                Account account = _accountService.read(accountId);
                Connection accountConnection = account.getConnection();
                log.debug("Syncing Transactions for Following Account: [{}]", account);
                String previousCursor = accountConnection.getPreviousCursor();
                String accessToken = accountConnection.getAccessToken();

                //Fetch Added, Modified, and Removed Transactions
                PlaidTransactionSyncResponseDto syncResponseDto = _plaidService.syncTransactions(accessToken, previousCursor);
                log.info("PlaidTransactionSyncResponseDto for Account ID {} : [{}]", accountId, syncResponseDto);

                //For each Added Transaction --> Map to Transaction Entities & Set Account/Category
                List<Transaction> addedTransactions = Optional.ofNullable(syncResponseDto.getAdded()).stream().flatMap(List::stream)
                        .map(_transactionMapper::toEntity)
                        .peek(transaction -> {
                            //TODO: Intelligently assign CategoryType & Category in future
                            transaction.setCategory(null);
                            transaction.setAccount(account);
                        })
                        .filter(transaction -> transaction.getAmount() > 0) //filter out transaction that have negative amounts
                        .filter(transaction -> GeneralUtil.isDateInCurrentMonth(transaction.getDate()))
                        .toList();
                log.debug("Updating DB With The Following Added Transactions: [{}]", addedTransactions);
                List<Transaction> persistedAddedTransactions = _transactionRepository.saveAllAndFlush(addedTransactions);

                //Add Persisted Transactions to List of Transactions to Return
                allModifiedOrAddedTransactions.addAll(persistedAddedTransactions);

                //For each Modified Transaction --> Map to Transaction Entities, Set Account/Category, Filter Out Transaction with Non-Existent IDs
                List<Transaction> modifiedTransactions = Optional.ofNullable(syncResponseDto.getModified()).stream().flatMap(List::stream)
                        .map(_transactionMapper::toEntity)
                        .filter(transaction -> _transactionRepository.existsById(transaction.getTransactionId())) //Ensure Transaction Exists within DB (filter out all updates pertaining to user updated Transactions)
                        .filter(transaction -> transaction.getAmount() > 0) //filter out transaction that have negative amounts
                        .filter(transaction -> GeneralUtil.isDateInCurrentMonth(transaction.getDate())) //filter out transactions not within current month
                        .peek(transaction -> {
                            //TODO: Intelligently assign CategoryType & Category in future
                            transaction.setCategory(null);
                            transaction.setAccount(account);
                        })
                        .toList();
                log.debug("Updating DB With The Following Modified Transactions: [{}]", modifiedTransactions);
                List<Transaction> persistedModifiedTransactions = _transactionRepository.saveAllAndFlush(modifiedTransactions);

                //Add All Persisted Modified Transactions to List of Transactions to Return
                allModifiedOrAddedTransactions.addAll(persistedModifiedTransactions);


                //Remove Transaction Associated with 'Removed' List from Plaid API
                List<Transaction> removedTransactions = Optional.ofNullable(syncResponseDto.getRemoved()).stream().flatMap(List::stream)
                                .map(_transactionMapper::toEntity)
                                .toList();
                log.debug("Removing following Transaction due to Plaid API marking them as 'removed': [{}]", removedTransactions);
                _transactionRepository.deleteAll(removedTransactions);


                //Update Cursor for Connection & Persist
                accountConnection.setPreviousCursor(syncResponseDto.getNext_cursor());
                _connectionService.update(accountConnection, accountConnection.getConnectionId());

            } catch (PlaidServiceException plaidServiceException){
               log.error("PlaidServiceException occurred while syncing transactions via our TransactionService: [{}]", plaidServiceException.getMessage());
               throw plaidServiceException;
            }
            catch(RuntimeException e){
                log.error("An error occurred while Syncing Transactions: [{}]", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return allModifiedOrAddedTransactions;
    }

    @Override
    public List<Transaction> readAll(){
        log.info("Attempting to read all Transaction entities corresponding to authenticated user's added Accounts and the current month");
        User currentAuthUser = _userService.getCurrentAuthUser();
        List<Account> accounts = currentAuthUser.getAccounts();

        //Validate User Has Accounts To Fetch Transactions For
        if(accounts == null) {
            return new ArrayList<>();
        }

        //Fetch All Account IDs corresponding to authenticated user
        List<String> accountIds = accounts.stream()
                .map(Account::getAccountId)
                .collect(Collectors.toList());
        LocalDate currentDate = LocalDate.now();

        log.debug("Reading transactions that are within the same year/date as {} and corresponding to following account IDs: {}", currentDate, accountIds);
        return _transactionRepository.findByAccountIdsAndCurrentMonth(accountIds, currentDate);
    }

    @Override
    public Transaction readById(String transactionId) throws RuntimeException{
        log.info("Attempting to read Transaction by the following ID: [{}]", transactionId);
        return _transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction with the following ID not found: " + transactionId));
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
    public List<Transaction> splitTransaction(String transactionId, SplitTransactionDto splitTransactionDto) throws RuntimeException{
        log.info("Attempting to split out Transaction with the ID {}", transactionId);

        //Fetch Original Transaction by ID
        Transaction originalTransaction = readById(transactionId);

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

        //Atomic Integer for Incremental Suffixes
        AtomicInteger counter = new AtomicInteger(1);

        //Map TransactionDto's to Transaction entities
        List<Transaction> splitTransactions = updatedTransactionDtos.stream()
                .map(_transactionMapper::toEntity)
                .peek(transaction -> transaction.setTransactionId(transactionId + "_" + counter.getAndIncrement()))
                .toList();

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
}
