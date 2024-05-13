package com.bavis.budgetapp.dto;

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
public class TransactionSyncRequestDto {
    private List<String> accounts;
}
