package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.dto.SplitTransactionDto;
import com.bavis.budgetapp.dto.SyncTransactionsDto;
import com.bavis.budgetapp.dto.TransactionDto;
import com.bavis.budgetapp.dto.AccountsDto;
import com.bavis.budgetapp.entity.Transaction;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Service for handling our business logic with Transaction Entities
 */
public interface TransactionService {

    /**
     * Functionality for syncing a set of user Account's Transaction entities (added, removed, modified)
     *
     * @param accountsDto
     *          - DTO contain list of relevant Account IDs's
     * @return
     *          - all modified/created Transactions and all removed Transaction ids
     */
    SyncTransactionsDto syncTransactions(AccountsDto accountsDto);

    /**
     * Functionality to fetch all Transaction entities corresponding to authenticated User
     *
     * @return
     *      - all Transactions corresponding to Auth User
     */
    List<Transaction> readAll();


    /**
     * Functionality for fetching all relevant Transaction entities corresponding to a particular Category
     *
     * @param categoryId
     *          - CategoryId pertaining to Category to fetch Transactions for
     * @return
     *          - all Transactions corresponding to passed in Category ID
     */
    List<Transaction> fetchCategoryTransactions(long categoryId);

    /**
     * Functionality to create a Transaction entity independent of any Account entity.
     * This is utilized to allow users to create mock Transactions for accounts they are unable to add via Plaid API.
     *
     * @param transactionDto
     *          - TransactionDTO to map into a Transaction entity
     * @return
     *          - persisted Transaction entity
     */
    Transaction addTransaction(TransactionDto transactionDto);


    /**
     * Functionality to reduce the amount associated with a particular Transaction entity
     *
     * @param transactionId
     *          - ID of Transaction entity to update
     * @param transactionDto
     *          - TransactionDto containing updates to Transaction amount
     * @return
     *          - updated Transaction entity
     */
    Transaction reduceTransactionAmount(String transactionId, TransactionDto transactionDto);


    /**
     * Functionality to split a Transaction into multiple Transaction entities
     *
     * @param transactionId
     *          - Transaction ID of original Transaction entity to split out
     * @param splitTransactionDto
     *          - List of new split out Transactions
     * @return
     *          - new Transaction entities split out from original
     */
    List<Transaction> splitTransaction(String transactionId, SplitTransactionDto splitTransactionDto);

    /**
     * Functionality to assign a Transaction a Category
     *
     * @param assignCategoryRequestDto
     *          - DTO containing Category ID and Transaction ID
     * @return
     *          - updated Transaction
     */
    Transaction assignCategory(AssignCategoryRequestDto assignCategoryRequestDto);

    /**
     * Functionality to rename a Transaction to a more relevant name
     *
     * @param transactionId
     *          - ID of Transaction to be updated
     * @param updatedName
     *          - Updated Transaction name
     * @return
     *          - updated Transaction entity
     */
    Transaction updateTransactionName(String transactionId, String updatedName);

    /**
     * Functionality to read a Transaction by its ID
     *
     * @param transactionId
     *          - ID of Transaction to fetch
     * @return
     *          - Transaction corresponding to ID
     */
    Transaction readById(String transactionId);

    /**
     * Functionality to remove an assigned Category from a Transaction
     *
     * @param transactionId
     *          - transaction ID corresponding to Transaction entity to update
     */
    void removeAssignedCategory(String transactionId);

    /**
     * Functionality to delete a Transaction
     *
     * @param transactionId
     *          - transaction ID corresponding to Transaction to delete
     */
    void deleteTransaction(String transactionId);


    /**
     * Functionality to remove all Transactions corresponding to a particular Account
     *
     * @param accountId
     *          - Account ID to remove Transactions for
     */
    void removeAccountTransactions(String accountId);

}
