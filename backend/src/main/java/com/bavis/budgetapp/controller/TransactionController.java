package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Controller utilized for working with Transaction entity
 */
@RestController
@Log4j2
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService _transactionService;

    /**
     * Sync all Transaction with external financial institutions
     *
     * @param transactionSyncRequestDto
     *          - DTO storing all Account IDs to sync transactions for
     * @return
     *          - all modified/added transactions
     */
    @PostMapping("/sync")
    public ResponseEntity<List<Transaction>> syncTransactions(@Valid @RequestBody TransactionSyncRequestDto transactionSyncRequestDto){
        log.info("Received request to SyncTransactions for following TransactionSyncRequest: [{}]", transactionSyncRequestDto);
        return ResponseEntity.ok(_transactionService.syncTransactions(transactionSyncRequestDto));
    }

    /**
     * Retrieve all Transaction entities within the current month corresponding to Authenticated User's added Accounts
     *
     * @return
     *      - all Transactions within current month for Accounts associated with authenticated user
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> readAll() {
        log.info("Received request to read all Transactions for current month for authenticated user");
        return ResponseEntity.ok(_transactionService.readAll());
    }

    /**
     * Assign a Transaction to a Category
     *
     * @param assignCategoryRequestDto
     *          - DTO containing Category ID and Transaction ID
     * @return
     *          - Updated Transaction
     */
    @PutMapping("/category")
    public ResponseEntity<Transaction> assignCategory(@Valid @RequestBody AssignCategoryRequestDto assignCategoryRequestDto) {
        log.info("Assigning Transaction to a Category via following request: [{}]", assignCategoryRequestDto);
        return ResponseEntity.ok(_transactionService.assignCategory(assignCategoryRequestDto));
    }
}
