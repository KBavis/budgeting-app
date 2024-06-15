package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidPercentAllocated;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for UpdateCategoryTypeDto for attribute percentAllocated
 *
 * @author Kellen Bavis
 */
public class UpdateCategoryTypeDtoPercentAllocatedValidator implements ConstraintValidator<UpdateCategoryTypeValidPercentAllocated, UpdateCategoryTypeDto> {
    @Override
    public void initialize(UpdateCategoryTypeValidPercentAllocated constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(UpdateCategoryTypeDto updateCategoryTypeDto, ConstraintValidatorContext constraintValidatorContext) {
        double percentAllocation = updateCategoryTypeDto.getBudgetAllocationPercentage();
        return percentAllocation > 0 && percentAllocation < 1;
    }
}
