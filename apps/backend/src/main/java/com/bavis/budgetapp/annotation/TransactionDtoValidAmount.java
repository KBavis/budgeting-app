package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.TransactionDtoAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to ensure that TransactionDto has a valid amount associated with it
 *
 * @author Kellen Bavis
 */
@Constraint(validatedBy = TransactionDtoAmountValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionDtoValidAmount {
    String message() default "The provided transaction amount is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

