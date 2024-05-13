package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.service.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {
    @Override
    public List<Transaction> syncTransactions(TransactionSyncRequestDto transactionSyncRequestDto) {
        //TODO: Finish me
        return null;
    }
}
