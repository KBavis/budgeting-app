package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidCategoryType;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        CategoryType categoryType = categoryTypeService.read(categoryDto.getCategoryTypeId());
        if(categoryType != null) {
           return Objects.equals(categoryType.getUser().getUserId(), userService.getCurrentAuthUser().getUserId());
        }
        return false;
    }
}
