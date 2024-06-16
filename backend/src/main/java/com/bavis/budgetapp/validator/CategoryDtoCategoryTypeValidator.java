package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidCategoryType;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Kellen Bavis
 *
 * Validation Class to ensure that our passed in CategoryDto has a valid CategoryType associated with it
 *          - CategoryType ID references valid CategoryType attribute associated with authenticated user making request
 */
@Component
public class CategoryDtoCategoryTypeValidator implements ConstraintValidator<CategoryDtoValidCategoryType, CategoryDto> {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private UserService userService;

    @Override
    public void initialize(CategoryDtoValidCategoryType constraintAnnotation) {
        //nothing to initialize
    }

    /**|
     * Validates that our CategoryType attribute for our CategoryDto is valid
     *
     * @param categoryDto
     *          - CategoryDto to validate CategoryType for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the passed in CategoryType for our CategoryDto
     */
    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        CategoryType categoryType = null;
        boolean valid = false;

        try{
            categoryType = categoryTypeService.read(categoryDto.getCategoryTypeId());
        } catch(Exception e){
            if(e.getMessage().contains("Invalid category type id:")){
                return false;
            }
        }

        if(categoryType != null) {
           valid = Objects.equals(categoryType.getUser().getUserId(), userService.getCurrentAuthUser().getUserId());
           return valid;
        }
        return valid;
    }
}
