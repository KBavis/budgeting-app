package com.bavis.budgetapp.annotation;


import com.bavis.budgetapp.validator.IncomeDtoAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IncomeDtoAmountValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncomeDtoValidAmount {
    String message() default "The provided income amount is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
