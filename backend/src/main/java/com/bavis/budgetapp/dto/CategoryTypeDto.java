package com.bavis.budgetapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO to Pass All Updates To Percent Budget Allocation of a Given User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTypeDto {
    String name;

    double budgetAllocationPercentage;
}
