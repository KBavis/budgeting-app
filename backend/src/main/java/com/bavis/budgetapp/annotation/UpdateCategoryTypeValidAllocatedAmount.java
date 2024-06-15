package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.UpdateCategoryTypeDtoAmountAllocatedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation utilized for validating that a UpdateCategoyrTypeDto has a valid amount associated with it
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdateCategoryTypeDtoAmountAllocatedValidator.class)
public @interface UpdateCategoryTypeValidAllocatedAmount {
    String message() default "The provided CategoryType allocation amount is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
