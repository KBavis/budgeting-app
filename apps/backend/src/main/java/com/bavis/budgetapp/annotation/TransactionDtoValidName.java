package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.TransactionDtoNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to apply to TransactionDto to ensure valid name
 *
 * @author Kellen Bavis
 */
@Constraint(validatedBy = TransactionDtoNameValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionDtoValidName {
    String message() default "The provided transaction name is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
