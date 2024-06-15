package com.bavis.budgetapp.validator;


import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidAllocatedAmount;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for UpdateCategoryTypeDto allocatedAmount attribute
 *
 * @author Kellen Bavis
 */
public class UpdateCategoryTypeDtoAmountAllocatedValidator implements ConstraintValidator<UpdateCategoryTypeValidAllocatedAmount, UpdateCategoryTypeDto> {

    @Override
    public void initialize(UpdateCategoryTypeValidAllocatedAmount constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(UpdateCategoryTypeDto updateCategoryTypeDto, ConstraintValidatorContext constraintValidatorContext) {
        double amountAllocated = updateCategoryTypeDto.getAmountAllocated();
        return amountAllocated > 0;
    }
}
