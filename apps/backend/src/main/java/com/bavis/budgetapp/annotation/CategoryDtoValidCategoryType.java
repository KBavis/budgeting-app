package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.CategoryDtoCategoryTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation utilized for validating that a proper CategoryType was referenced in the request
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryDtoCategoryTypeValidator.class)
public @interface CategoryDtoValidCategoryType {
    String message() default "Category must have a valid association to a CategoryType";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
