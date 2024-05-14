package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.TransactionSyncRequestValidAccounts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * DTO utilized to store list of Account IDs needing to be synced
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TransactionSyncRequestValidAccounts
public class TransactionSyncRequestDto {
    private List<String> accounts;
}
