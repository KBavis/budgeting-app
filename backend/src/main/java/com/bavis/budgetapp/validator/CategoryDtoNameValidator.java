package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidName;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryDtoNameValidator implements ConstraintValidator<CategoryDtoValidName, CategoryDto>{

    @Override
    public void initialize(CategoryDtoValidName constraintAnnotation) {
        //nothing to initialize
    }

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        String REGEX = "^[a-zA-Z]{1,29}$";
        return categoryDto != null && categoryDto.getName() != null && !categoryDto.getName().isEmpty() && categoryDto.getName().matches(REGEX);
    }
}
