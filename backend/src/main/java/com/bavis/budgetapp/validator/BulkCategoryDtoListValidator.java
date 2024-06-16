package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.BulkCategoryDtoValidList;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

/**
 * @author Kellen Bavis
 *
 * Validation class for ensuring BulkCategoryDto contains valid list of categories
 *          - Ensures that provided list of CategoryDto at least contains one entry
 */
public class BulkCategoryDtoListValidator implements ConstraintValidator<BulkCategoryDtoValidList, BulkCategoryDto> {

    @Override
    public void initialize(BulkCategoryDtoValidList constraintAnnotation) {
        //nothing to initialize
    }

    /**
     *
     * Validates the list of CategoryDto passed as attribute for our BulkCategoryDto
     *
     * @param categories
     *          - BulkCategoryDto containing list of CategoryDto
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of our list of CategoryDto in BulkCategoryDto
     */
    @Override
    public boolean isValid(BulkCategoryDto categories, ConstraintValidatorContext constraintValidatorContext) {
        return categories != null && categories.getCategories() != null && !categories.getCategories().isEmpty();
    }
}
