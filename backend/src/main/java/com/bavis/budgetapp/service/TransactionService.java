package com.bavis.budgetapp.service;

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

}
