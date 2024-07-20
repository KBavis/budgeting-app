package com.bavis.budgetapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditCategoryDto {
    private String name;
    private double budgetAllocationPercentage;
    private List<UpdateCategoryDto> updatedCategories;
}
