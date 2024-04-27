package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.IncomeDtoValidAmount;
import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

//todo: consider adding custom validations for errors with NotNull/NotEmpty so message is more meaningful in response
@IncomeDtoValidAmount
@Builder
@Data
public class IncomeDto {

    private double amount;

    @NotNull
    private IncomeType incomeType;

    @NotNull
    private IncomeSource incomeSource;

    @NotEmpty
    private String description;
}
