package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.AuthRequestNameValidator;
import com.bavis.budgetapp.validator.AuthRequestSamePasswordsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AuthRequestNameValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequestValidName {
    String message() default "The provided name is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
