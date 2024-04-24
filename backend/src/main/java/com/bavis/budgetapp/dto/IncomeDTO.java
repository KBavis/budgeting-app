package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.enumeration.IncomeSource;
import com.bavis.budgetapp.enumeration.IncomeType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class IncomeDTO {

    private double amount;

    private IncomeType incomeType;

    private IncomeSource incomeSource;

    private String description;
}
