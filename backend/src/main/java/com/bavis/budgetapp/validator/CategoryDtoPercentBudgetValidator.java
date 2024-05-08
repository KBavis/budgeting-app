package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidPercentBudgetAllocation;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * @author Kellen Bavis
 *
 * Validates the validity of the percent allocated for a CategoryDto
 *          - ensures the percent is greater than 1%, and less than 100%
 */
@Log4j2
public class CategoryDtoPercentBudgetValidator implements ConstraintValidator<CategoryDtoValidPercentBudgetAllocation, CategoryDto>{

    @Override
    public void initialize(CategoryDtoValidPercentBudgetAllocation constraintAnnotation) {
        //nothing to initialize
    }

    /**
     * Validates that the percent allocated is a valid percentage
     *
     * @param categoryDto
     *           - CategoryDto to validate percent for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the passed in budget allocation percentage for CategoryDto
     */
    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = categoryDto != null && categoryDto.getBudgetAllocationPercentage() > 0 && categoryDto.getBudgetAllocationPercentage() < 100;
        log.debug("Validity of passed in budget percent allocation for CategoryDto: [{}]", categoryDto);
        return  valid;
    }
}
