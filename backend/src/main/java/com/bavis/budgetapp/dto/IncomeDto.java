package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.IncomeDtoValidAmount;
import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate needed information for creating a new Income for a User
 */
@IncomeDtoValidAmount
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeDto {

    private double amount;

    @NotNull(message = "incomeType must not be null")
    private IncomeType incomeType;

    @NotNull(message = "incomeSource must not be null")
    private IncomeSource incomeSource;

    @NotEmpty(message = "description must not be empty")
    private String description;
}
