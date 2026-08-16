package com.bavis.budgetapp.dto.response;

import com.bavis.budgetapp.constants.AccountType;
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
}
