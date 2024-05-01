package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidPercentBudgetAllocation;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CategoryDtoPercentBudgetValidator implements ConstraintValidator<CategoryDtoValidPercentBudgetAllocation, CategoryDto>{

    @Override
    public void initialize(CategoryDtoValidPercentBudgetAllocation constraintAnnotation) {
        //nothing to initialize
    }

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = categoryDto != null && categoryDto.getBudgetAllocationPercentage() > 0 && categoryDto.getBudgetAllocationPercentage() < 100;
        log.debug("CategoryDtoPercentBudgetAllocation: {}", valid);
        return  valid;
    }
}
