package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncTransactionsDto {
    private List<Transaction> allModifiedOrAddedTransactions;
    private List<String> removedTransactionIds;
}
