package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.CategoryDtoValidBudgetAmount;
import com.bavis.budgetapp.annotation.CategoryDtoValidName;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CategoryDtoNameValidator implements ConstraintValidator<CategoryDtoValidName, CategoryDto>{

    @Override
    public void initialize(CategoryDtoValidName constraintAnnotation) {
        //nothing to initialize
    }

    @Override
    public boolean isValid(CategoryDto categoryDto, ConstraintValidatorContext constraintValidatorContext) {
        String REGEX = "^[a-zA-Z\\s]{1,49}$";
        boolean valid = categoryDto != null && categoryDto.getName() != null && !categoryDto.getName().isEmpty()
                && categoryDto.getName().matches(REGEX);
        log.debug("CategoryDtoNameValidator: {}", valid);
        return valid;
    }
}
