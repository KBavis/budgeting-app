package com.bavis.budgetapp.dto.response;

import com.bavis.budgetapp.constants.AccountType;
import com.bavis.budgetapp.constants.ConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Response DTO encapsulating Account entity data merged with point-in-time VT attributes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private String accountId;
    private String accountName;
    private AccountType accountType;
    private double balance;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Health of the Account's connection to their financial institution
     */
    private ConnectionStatus connectionStatus;

    /**
     * Plaid error code explaining an unhealthy connection (null when healthy)
     */
    private String connectionErrorCode;

    /**
     * True if the User must log in to their financial institution again (Plaid Link update mode) before syncing works
     */
    private boolean requiresReauth;
}
