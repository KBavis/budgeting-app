package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.TransactionDtoDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to apply to TransactionDto to ensure valid date
 *
 * @author Kellen Bavis
 */
@Constraint(validatedBy = TransactionDtoDateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionDtoValidDate {
    String message() default "The provided transaction date is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}