package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author  Kellen Bavis
 *
 * DTO to store the needed information to sync transactions with Plaid API
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaidTransactionSyncRequestDto {
    private String client_id;
    private String secret;
    private String access_token;
    private int count;
    private String cursor;
}
