package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.RenameCategoryDtoValidName;
import com.bavis.budgetapp.dto.RenameCategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for ensuring the RenameCategoryDto has a valid name
 *
 * @author Kellen Bavis
 */
public class RenameCategoryDtoValidator implements ConstraintValidator<RenameCategoryDtoValidName, RenameCategoryDto> {

    @Override
    public void initialize(RenameCategoryDtoValidName constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(RenameCategoryDto renameCategoryDto, ConstraintValidatorContext constraintValidatorContext) {
        if(renameCategoryDto == null || renameCategoryDto.getCategoryName() == null) { return false; }

        String regex = "^[a-zA-Z\s]{1,30}$";
        return renameCategoryDto.getCategoryName().matches(regex);
    }
}
