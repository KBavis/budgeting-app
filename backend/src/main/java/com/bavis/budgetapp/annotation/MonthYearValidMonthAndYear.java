package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.MonthYearValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MonthYearValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MonthYearValidMonthAndYear {
    String message() default "The provided month and/or year is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
