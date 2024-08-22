package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.UpdateCategoryTypeDtoSavedAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation utilized for validating that a UpdateCategoyrTypeDto has a valid saved amount associated with it
 *
 * @author Kellen Bavis
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdateCategoryTypeDtoSavedAmountValidator.class)
public @interface UpdateCategoryTypeValidSavedAmount {
    String message() default "The provided CategoryType saved amount is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
