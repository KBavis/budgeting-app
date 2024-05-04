package com.bavis.budgetapp.annotation;


import com.bavis.budgetapp.validator.IncomeDtoAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation for validating that a passed in income is valid
 */
@Constraint(validatedBy = IncomeDtoAmountValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncomeDtoValidAmount {
    String message() default "The provided income amount is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
