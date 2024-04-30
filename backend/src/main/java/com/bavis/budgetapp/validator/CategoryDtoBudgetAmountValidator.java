package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryDtoBudgetAmountValidator implements ConstraintValidator<CategoryDtoValidBudgetAmount, CategoryDto> {

    @Override
    public void initialize(CategoryDtoValidBudgetAmount constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        return categoryDto.getBudgetAmount() > 0;
    }
}
