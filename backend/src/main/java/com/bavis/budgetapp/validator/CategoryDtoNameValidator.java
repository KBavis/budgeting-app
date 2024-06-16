package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidName;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Kellen Bavis
 *
 * Validates that the CategoryDto name attribute paassed in is valid
 *          - Ensures that the length of the 'name' attribute is between 1 and 49 characters
 */
public class CategoryDtoNameValidator implements ConstraintValidator<CategoryDtoValidName, CategoryDto>{

    @Override
    public void initialize(CategoryDtoValidName constraintAnnotation) {
        //nothing to initialize
    }

    /**
     * Validates the name passed in for a corresponding CategoryDto
     *
     * @param categoryDto
     *          - CategoryDto to validate name attribute for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the name attribute passed in for CategoryDto
     */
    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        if(categoryDto != null){
           String categoryName = categoryDto.getName();
           if(!StringUtils.isBlank(categoryName)){
               return categoryName.length() < 49; //49 is max length
           }
        }
        return false;
    }
}
