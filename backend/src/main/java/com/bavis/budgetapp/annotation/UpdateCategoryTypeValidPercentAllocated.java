package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.UpdateCategoryTypeDtoPercentAllocatedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation utilized for validating that a UpdateCategoyrTypeDto has a valid percent allocation associated with it
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdateCategoryTypeDtoPercentAllocatedValidator.class)
public @interface UpdateCategoryTypeValidPercentAllocated {
    String message() default "The provided CategoryType percent allocation is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
