package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.CategoryDtoBudgetAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryDtoBudgetAmountValidator.class)
public @interface CategoryDtoValidBudgetAmount {
    String message() default "Budget Amount must be a valid, positive numeric value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
