package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.BulkCategoryDtoValidList;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

/**
 * Validation for annotation BulkCategoryDtoValidList
 */
@Log4j2
public class BulkCategoryDtoListValidator implements ConstraintValidator<BulkCategoryDtoValidList, BulkCategoryDto> {

    @Override
    public void initialize(BulkCategoryDtoValidList constraintAnnotation) {
        //nothing to initialize
    }

    /**
     * @param categories - Categories DTO
     * @param constraintValidatorContext - Constraint Context
     * @return - Validity of BulkCategoryDto
     */
    @Override
    public boolean isValid(BulkCategoryDto categories, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = categories != null && categories.getCategories() != null && !categories.getCategories().isEmpty();
        log.info("BulkCategoryDtoListValidator: {}", valid);
        return  valid;
    }
}
