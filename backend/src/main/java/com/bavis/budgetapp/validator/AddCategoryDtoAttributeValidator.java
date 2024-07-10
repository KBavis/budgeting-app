package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AddCategoryDtoValidAttributes;
import com.bavis.budgetapp.dto.AddCategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for AddCategoryDto to ensure attributes are not null
 *
 * @author Kellen Bavis
 */
public class AddCategoryDtoAttributeValidator implements ConstraintValidator<AddCategoryDtoValidAttributes, AddCategoryDto> {
    @Override
    public void initialize(AddCategoryDtoValidAttributes constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(AddCategoryDto addCategoryDto, ConstraintValidatorContext constraintValidatorContext) {
        return addCategoryDto.getAddedCategory() != null && addCategoryDto.getUpdatedCategories() != null;
    }
}
