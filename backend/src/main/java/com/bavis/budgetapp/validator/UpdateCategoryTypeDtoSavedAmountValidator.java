package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidSavedAmount;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * Validator for UpdateCategoryTypeDto for attribute amountSaved
 *
 * @author Kellen Bavis
 */
public class UpdateCategoryTypeDtoSavedAmountValidator implements ConstraintValidator<UpdateCategoryTypeValidSavedAmount, UpdateCategoryTypeDto> {
    @Override
    public void initialize(UpdateCategoryTypeValidSavedAmount constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(UpdateCategoryTypeDto updateCategoryTypeDto, ConstraintValidatorContext constraintValidatorContext) {
        double amountSaved = updateCategoryTypeDto.getSavedAmount();
        return amountSaved >= 0;
    }
}
