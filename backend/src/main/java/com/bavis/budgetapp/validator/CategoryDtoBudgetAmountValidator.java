package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CategoryDtoBudgetAmountValidator implements ConstraintValidator<CategoryDtoValidBudgetAmount, CategoryDto> {

    @Override
    public void initialize(CategoryDtoValidBudgetAmount constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = categoryDto.getBudgetAmount() > 0;
        log.debug("CategoryDtoBudgetAmountValidator: {}", valid);
        return valid;
    }
}
