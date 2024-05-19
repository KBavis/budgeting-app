package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
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

    private TransactionRepository _transactionRepository;

    private TransactionMapper _transactionMapper;



    public TransactionServiceImpl(PlaidServiceImpl plaidService, AccountServiceImpl accountService, TransactionMapper transactionMapper, TransactionRepository transactionRepository, ConnectionServiceImpl connectionService) {
        this._transactionRepository = transactionRepository;
        this._plaidService = plaidService;
        this._accountService = accountService;
        this._transactionMapper = transactionMapper;
        this._connectionService = connectionService;
    }

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
}
