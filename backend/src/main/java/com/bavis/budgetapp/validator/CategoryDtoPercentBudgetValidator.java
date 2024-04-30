package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidPercentBudgetAllocation;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryDtoPercentBudgetValidator implements ConstraintValidator<CategoryDtoValidPercentBudgetAllocation, CategoryDto>{

    @Override
    public void initialize(CategoryDtoValidPercentBudgetAllocation constraintAnnotation) {
        //nothing to initialize
    }

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        return categoryDto != null && categoryDto.getBudgetAllocationPercentage() > 0 && categoryDto.getBudgetAllocationPercentage() < 100;
    }
}
