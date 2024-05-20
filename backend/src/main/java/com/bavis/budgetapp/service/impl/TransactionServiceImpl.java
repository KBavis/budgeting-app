package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.dto.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.*;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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

                //Combine Added & Modified List Together, Map to Transaction Entities, Set Account/Category
                List<Transaction> addedOrModifiedTransactions = Stream.concat(
                                Optional.ofNullable(syncResponseDto.getAdded()).stream().flatMap(List::stream),
                                Optional.ofNullable(syncResponseDto.getModified()).stream().flatMap(List::stream)
                        )
                        .map(_transactionMapper::toEntity)
                        .peek(transaction -> {
                            //TODO: Intelligently assign CategoryType & Category in future
                            transaction.setCategory(null);
                            transaction.setAccount(account);
                        })
                        .filter(transaction -> transaction.getAmount() > 0) //filter out transaction that have negative amounts
                        .filter(transaction -> GeneralUtil.isDateInCurrentMonth(transaction.getDate()))
                        .toList();
                log.debug("Updating DB With The Following Added or Modified Transactions: [{}]", addedOrModifiedTransactions);
                List<Transaction> persistedTransactions = _transactionRepository.saveAllAndFlush(addedOrModifiedTransactions);

                //Add Persisted Transactions to List of Transactions to Return
                allModifiedOrAddedTransactions.addAll(persistedTransactions);


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
}
