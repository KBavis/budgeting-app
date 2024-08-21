package com.bavis.budgetapp.annotation;


import com.bavis.budgetapp.validator.AssignCategoryRequestUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for ensuring our AssignCategoryRequest pertains to currently logged in user
 *
 * @author Kellen Bavis
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = AssignCategoryRequestUserValidator.class)
public @interface AssignCategoryRequestValidUser {
    String message() default "The provided Category ID and Transaction ID do not correspond to Authenticated User.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
