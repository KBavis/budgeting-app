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

@Component
@Log4j2
public class CategoryDtoCategoryTypeValidator implements ConstraintValidator<CategoryDtoValidCategoryType, CategoryDto> {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private UserService userService;

    @Override
    public void initialize(CategoryDtoValidCategoryType constraintAnnotation) {
        //nothing to initialize
    }

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        CategoryType categoryType = null;
        boolean valid = false;

        //Handle potential RuntimeException
        try{
            categoryType = categoryTypeService.read(categoryDto.getCategoryTypeId());
        } catch(Exception e){
            if(e.getMessage().contains("Invalid category type id:")){
                return false;
            }
        }

        if(categoryType != null) {
           valid = Objects.equals(categoryType.getUser().getUserId(), userService.getCurrentAuthUser().getUserId());
           log.debug("CategoryDtoCategoryTypeValidator: {}", valid);
           return valid;
        }
        return valid;
    }
}
