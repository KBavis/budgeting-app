package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.dto.SplitTransactionDto;
import com.bavis.budgetapp.dto.TransactionDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.validator.group.TransactionDtoAddValidationGroup;
import com.bavis.budgetapp.validator.group.TransactionDtoSplitValidationGroup;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
     * Add a Transaction entity that is independent of any financial institution
     *
     * @param transactionDto
     *          - TransactionDto utilized to create Transaction entity
     * @return
     *          - Newly created Transaction entity
     */
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody @Validated(TransactionDtoAddValidationGroup.class) TransactionDto transactionDto) {
        log.info("Received request to create new Transaction entity for following TransactionDto: [{}]", transactionDto);
        return ResponseEntity.ok(_transactionService.addTransaction(transactionDto));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> reduceTransactionAmount(@PathVariable("transactionId") String transactionId, @RequestBody TransactionDto transactionDto) {
        log.info("Received request to reduce Transaction amount via following TransactionDto: [{}]", transactionDto);
        return ResponseEntity.ok(_transactionService.reduceTransactionAmount(transactionId, transactionDto));
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

    /**
     * Split a Transaction into multiple Transaction entities
     *
     * @param transactionId
     *          - ID pertaining to original Transaction to split out
     * @param splitTransactionDto
     *          - List of split out Transactions
     * @return
     *          - List of new Transaction entities
     */
    @PutMapping("/{transactionId}/split")
    public ResponseEntity<List<Transaction>> splitTransactions(@PathVariable("transactionId") String transactionId, @Validated(TransactionDtoSplitValidationGroup.class) @RequestBody SplitTransactionDto splitTransactionDto) {
        log.info("Received request to split out Transaction with ID {} into following Transactions: [{}]", transactionId, splitTransactionDto);
        return ResponseEntity.ok(_transactionService.splitTransaction(transactionId, splitTransactionDto));
    }

    /**
     * Remove assigned Category from a Transaction
     *
     * @param transactionId
                - transaction ID to remove assigned Category for
     */
    @DeleteMapping ("/{transactionId}/category")
    public void removeCategory(@PathVariable("transactionId") String transactionId){
         log.info("Received request to remove assigned category for the following Transaction with the ID {}", transactionId);
         _transactionService.removeAssignedCategory(transactionId);
    }


    @DeleteMapping("/{transactionId}")
    public void removeTransaction(@PathVariable("transactionId") String transactionId) {
        log.info("Received request to remove Transaction with the ID: {}", transactionId);
        _transactionService.deleteTransaction(transactionId);
    }
}
