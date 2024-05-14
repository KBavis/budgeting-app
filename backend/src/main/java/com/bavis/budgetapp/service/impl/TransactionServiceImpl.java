package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.dto.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.service.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    private TransactionRepository _transactionRepository;



    public TransactionServiceImpl(PlaidServiceImpl _plaidService, AccountServiceImpl accountService, TransactionRepository _transactionRepository) {
        this._transactionRepository = _transactionRepository;
        this._plaidService = _plaidService;
        this._accountService = accountService;
    }
    @Override
    public List<Transaction> syncTransactions(TransactionSyncRequestDto transactionSyncRequestDto) {
        List<Transaction> addedOrModifiedTransactions = new ArrayList<>();

        //Sync Transaction for each specified Account
        for(String accountId: transactionSyncRequestDto.getAccounts()){
            try{
                //Extract Access Token and Previous Cursor for Specified Account
                Account account = _accountService.read(accountId);
                Connection accountConnection = account.getConnection();
                String previousCursor = accountConnection.getPreviousCursor();
                String accessToken = accountConnection.getAccessToken();

                //Fetch Added, Modified, and Removed Transactions
                PlaidTransactionSyncResponseDto syncResponseDto = _plaidService.syncTransactions(accessToken, previousCursor);
                log.info("PlaidTransactionSyncResponseDto for Account ID {} : [{}]", accountId, syncResponseDto);

                //Update/Create Transactions
                //TODO: Add error handling for the cases where ADDED or MODIFIED is null
                List<PlaidTransactionDto> addedOrModifiedTransactionsDtos = Stream.concat(
                        syncResponseDto.getAdded().stream(),
                        syncResponseDto.getModified().stream()
                )
                        .toList();

                for(PlaidTransactionDto plaidTransactionDto: addedOrModifiedTransactionsDtos) {
                    log.info("PlaidTransactionDto to be ADDED or UPDATED: [{}]", plaidTransactionDto);

                    //Map To Transaction Entity

                    //Persist
                }

                //Update

                /**
                 *  TODO:
                 *      For each PlaidTransactionDto in 'added' list, map to Transaction Entity and Insert
                 *      For each PlaidTransactioNDto in 'modified' list, map to Transaction Entity and Update
                 *      For each PlaidTransactioNDto in 'removed' list, delete by Transaction ID
                 */

                //TODO: Update Connection ID with New Cursor from Response and Persist Changes


            } catch(RuntimeException e){
                log.error("An error occurred while fetching relevant Account entities for Syncing Transactions: [{}]", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
