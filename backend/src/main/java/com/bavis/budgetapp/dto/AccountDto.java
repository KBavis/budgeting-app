package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.constants.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String accountName;
    private double balance;
    private AccountType accountType;
}
