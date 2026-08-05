package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for responding to GET /transactions requests, separating
 * current-month transactions from unassigned previous-month transactions.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchTransactionsDto {
    private List<Transaction> currentMonthTransactions;
    private List<Transaction> unassignedPreviousMonthTransactions;
}
