package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidCategoryType;
import com.bavis.budgetapp.annotation.CategoryDtoValidName;
import com.bavis.budgetapp.annotation.CategoryDtoValidPercentBudgetAllocation;
import com.bavis.budgetapp.validator.group.CategoryDtoValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@CategoryDtoValidCategoryType(groups = CategoryDtoValidationGroup.class)
@CategoryDtoValidName(groups = CategoryDtoValidationGroup.class)
@CategoryDtoValidPercentBudgetAllocation(groups = CategoryDtoValidationGroup.class)
@CategoryDtoValidBudgetAmount(groups = CategoryDtoValidationGroup.class)
public class CategoryDto {
    private String name;
    private double budgetAllocationPercentage;
    private long categoryTypeId;
    private double budgetAmount;
}
