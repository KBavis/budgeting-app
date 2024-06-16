package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * @author Kellen Bavis
 *
 * Validation class for ensuring CategoryDto contains valid 'budgetamount' attribute
 *          - Ensures the allocated amount is greater than 0
 */
public class CategoryDtoBudgetAmountValidator implements ConstraintValidator<CategoryDtoValidBudgetAmount, CategoryDto> {

    @Override
    public void initialize(CategoryDtoValidBudgetAmount constraintAnnotation) {
        //nothing to initalize
    }

    /**
     * Validates that our CategoryDto has a valid budget amount
     *
     * @param categoryDto
     *          - CategoryDto to verify the budget amount for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of our budget amount in our CategoryDto
     */
    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        return categoryDto.getBudgetAmount() > 0;
    }
}
