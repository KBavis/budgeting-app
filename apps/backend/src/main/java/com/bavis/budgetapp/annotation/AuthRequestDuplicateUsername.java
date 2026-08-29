package com.bavis.budgetapp.annotation;


import com.bavis.budgetapp.validator.AuthRequestDuplicateUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 *  Annotation for validating a registration request has a unique username
 */
@Constraint(validatedBy = AuthRequestDuplicateUsernameValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequestDuplicateUsername {
    String message() default "The provided username already exists.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
