package com.bavis.budgetapp.dto.request;

import com.bavis.budgetapp.constants.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate updates for Account entities
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountDto {
    private String accountId;
    private String accountName;
    private AccountType accountType;
    private Double balance;
}
