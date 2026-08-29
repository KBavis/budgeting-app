package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.BulkCategoryDtoListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation utilized for validating that a bulk creeation request of categories contains at least one category
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BulkCategoryDtoListValidator.class)
public @interface BulkCategoryDtoValidList {
    String message() default "Must include at least one Category";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
