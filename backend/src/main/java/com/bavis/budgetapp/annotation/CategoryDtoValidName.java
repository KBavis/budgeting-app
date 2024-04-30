package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.BulkCategoryDtoListValidator;
import com.bavis.budgetapp.validator.CategoryDtoNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryDtoNameValidator.class)
public @interface CategoryDtoValidName {

    String message() default "Category name must be under 30 characters.";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
