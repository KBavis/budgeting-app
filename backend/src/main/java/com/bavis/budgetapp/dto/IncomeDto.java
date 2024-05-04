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

    @NotNull(message = "incomeType must not be null")
    private IncomeType incomeType;

    @NotNull(message = "incomeSource must not be null")
    private IncomeSource incomeSource;

    @NotEmpty(message = "description must not be empty")
    private String description;
}
