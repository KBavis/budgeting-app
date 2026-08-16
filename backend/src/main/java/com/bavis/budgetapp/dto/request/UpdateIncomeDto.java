package com.bavis.budgetapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate updated Income entity attributes
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateIncomeDto {

    private Long incomeId;

    private double amount;
}
