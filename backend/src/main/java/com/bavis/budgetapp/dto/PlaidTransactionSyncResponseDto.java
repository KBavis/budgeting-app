package com.bavis.budgetapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * DTO to deserialize Plaid API's /transaction/sync response
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaidTransactionSyncResponseDto {
    private List<PlaidAccountDto> accounts;
    private List<PlaidTransactionDto> added;
    private List<PlaidTransactionDto> modified;
    private List<PlaidTransactionDto> removed;
    private String next_cursor;
    private boolean has_more;
}
