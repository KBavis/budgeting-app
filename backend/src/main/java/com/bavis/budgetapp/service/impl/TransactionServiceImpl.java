package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.TransactionService;
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
    public List<Transaction> syncTransactions(TransactionSyncRequestDto transactionSyncRequestDto) {
        List<Transaction> addedOrModifiedTransactions;
        List<Transaction> removedTransactions;

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
                addedOrModifiedTransactions = Stream.concat(
                                Optional.ofNullable(syncResponseDto.getAdded()).stream().flatMap(List::stream),
                                Optional.ofNullable(syncResponseDto.getModified()).stream().flatMap(List::stream)
                        )
                        .map(_transactionMapper::toEntity)
                        .peek(transaction -> {
                            //TODO: Intelligently assign CategoryType & Category in future
                            transaction.setCategory(null);
                            transaction.setAccount(account);
                        })
                        .toList();
                log.debug("Updating DB With The Following Added or Modified Transactions: [{}]", addedOrModifiedTransactions);
                _transactionRepository.saveAllAndFlush(addedOrModifiedTransactions);


                //Remove Transaction Associated with 'Removed' List from Plaid API
                removedTransactions = Optional.ofNullable(syncResponseDto.getRemoved()).stream().flatMap(List::stream)
                                .map(_transactionMapper::toEntity)
                                .toList();
                log.debug("Removing following Transaction due to Plaid API marking them as 'removed': [{}]", removedTransactions);
                _transactionRepository.deleteAll(removedTransactions);


                //Update Cursor for Connection & Persist
                accountConnection.setPreviousCursor(syncResponseDto.getNext_cursor());
                _connectionService.update(accountConnection, accountConnection.getConnectionId()); //TODO: Implement Update logic for Connection Service or use Create Method

                //Return Added or Modified Transactions
                return addedOrModifiedTransactions;

            } catch(RuntimeException e){
                log.error("An error occurred while Syncing Transactions: [{}]", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
