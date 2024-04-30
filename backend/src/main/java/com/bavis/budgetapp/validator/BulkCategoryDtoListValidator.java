package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.BulkCategoryDtoValidList;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validation for annotation BulkCategoryDtoValidList
 */
public class BulkCategoryDtoListValidator implements ConstraintValidator<BulkCategoryDtoValidList, BulkCategoryDto> {

    @Override
    public void initialize(BulkCategoryDtoValidList constraintAnnotation) {
        //nothing to initialize
    }

    /**
     *
     * @param categories
     *          - Categories DTO
     * @param constraintValidatorContext
     *          - Constraint Context
     * @return
     *      - Validity of BulkCategoryDto
     */
    @Override
    public boolean isValid(BulkCategoryDto categories, ConstraintValidatorContext constraintValidatorContext) {
        return categories != null && categories.getCategories() != null && !categories.getCategories().isEmpty() && categories.getCategories().get(0) != null;
    }
}
