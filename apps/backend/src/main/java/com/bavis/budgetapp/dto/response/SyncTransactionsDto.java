package com.bavis.budgetapp.dto.response;

import com.bavis.budgetapp.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kellen Bavis
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncTransactionsDto {
    private List<AccountResponseDto> updatedAccounts;
    private List<Transaction> allModifiedOrAddedTransactions;
    private List<Transaction> previousMonthTransactions;
    private List<String> removedTransactionIds;
}
