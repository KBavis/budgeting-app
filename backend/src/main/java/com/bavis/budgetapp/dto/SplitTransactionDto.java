package com.bavis.budgetapp.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO to house list of split out Transactions
 *
 * @author Kellen Bavis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SplitTransactionDto {
    @Valid
    private List<TransactionDto> splitTransactions;
}
