package com.bavis.budgetapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO describing a single Account that could not be synced, and why
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSyncFailureDto {

    private String accountId;

    /**
     * Display name of the Account (may be null if the Account could not be resolved)
     */
    private String accountName;

    /**
     * Plaid's error code, if Plaid provided one (i.e. ITEM_LOGIN_REQUIRED)
     */
    private String errorCode;

    /**
     * User-facing explanation of why the Account could not be synced
     */
    private String message;

    /**
     * Whether the User must log in to their financial institution again (Plaid Link update mode) to fix this Account
     */
    private boolean requiresReauth;
}
