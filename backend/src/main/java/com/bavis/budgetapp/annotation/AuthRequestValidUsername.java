package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.AuthRequestUsernameFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation utilized for validating that an usernmae is in the proper format
 */
@Constraint(validatedBy = AuthRequestUsernameFormatValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequestValidUsername {
    String message() default "The provided username is not in the proper format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
