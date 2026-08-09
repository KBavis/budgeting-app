package com.bavis.budgetapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateCategoryDto {
    private double budgetAllocationPercentage;
    private Long categoryId;
}
