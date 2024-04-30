package com.bavis.budgetapp.annotation;


import com.bavis.budgetapp.validator.BulkCategoryDtoListValidator;
import com.bavis.budgetapp.validator.CategoryDtoPercentBudgetValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryDtoPercentBudgetValidator.class)
public @interface CategoryDtoValidPercentBudgetAllocation {
    String message() default "Category must be a valid percentage between 1 - 100&";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}