package com.bavis.budgetapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateCategoryDto {
    private double budgetAllocationPercentage;
    private Long categoryId;
}
