package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.AuthRequestNameValidator;
import com.bavis.budgetapp.validator.AuthRequestPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Kellen Bavis '
 *
 * Annotation utilized for validating that a user has configured a valid password
 */
@Constraint(validatedBy = AuthRequestPasswordValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequestValidPassword {
    String message() default "The provided password is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
