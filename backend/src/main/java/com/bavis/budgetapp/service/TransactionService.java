package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.dto.SplitTransactionDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param transactionSyncRequestDto
     *          - DTO contain list of relevant Account IDs's
     * @return
     *          - List of all modified/created Transaction entities
     */
    List<Transaction> syncTransactions(TransactionSyncRequestDto transactionSyncRequestDto);

    /**
     * Functionality to fetch all Transaction entities corresponding to authenticated User
     *
     * @return
     *      - all Transactions corresponding to Auth User
     */
    List<Transaction> readAll();

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

}
