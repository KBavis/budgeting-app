package com.bavis.budgetapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceDTO {
    private String accountId;
    private double currentBalance;
}
