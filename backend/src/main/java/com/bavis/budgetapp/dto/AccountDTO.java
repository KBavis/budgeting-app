package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.enumeration.AccountType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDTO {
    private String accountName;
    private double balance;
    private AccountType accountType;
}
