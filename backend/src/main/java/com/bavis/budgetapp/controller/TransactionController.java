package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.service.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TODO: Finalize this implementation, add comments, add proper logging
 */
@RestController
@Log4j2
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService _transactionService;

    @PostMapping("/sync")
    public ResponseEntity<List<Transaction>> syncTransactions(@RequestBody TransactionSyncRequestDto transactionSyncRequestDto){
        log.info("Received request to SyncTransactions for following TransactionSyncRequest: [{}]", transactionSyncRequestDto);
        return ResponseEntity.ok(_transactionService.syncTransactions(transactionSyncRequestDto));
    }
}
