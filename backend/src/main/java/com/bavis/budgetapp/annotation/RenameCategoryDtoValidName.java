package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.RenameCategoryDtoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation for validating that the RenameCategoryDto has a valid name
 */
@Constraint(validatedBy = RenameCategoryDtoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RenameCategoryDtoValidName {
    String message() default "The provided name contain no numbers, symbols, and be between 1-30 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
