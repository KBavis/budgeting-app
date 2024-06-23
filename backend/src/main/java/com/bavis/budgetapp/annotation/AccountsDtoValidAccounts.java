package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.AccountsDtoAccountsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = AccountsDtoAccountsValidator.class)
public @interface AccountsDtoValidAccounts {
    String message() default "The provided list of Account ID's to sync transactions for contains at least one invalid entry";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
