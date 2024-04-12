package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidUsername;
import com.bavis.budgetapp.request.AuthRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AuthRequestUsernameFormatValidator implements ConstraintValidator<AuthRequestValidUsername, AuthRequest> {

    //Validates all characters are a letter ,digit, underscore, or hyphen AND is between 8 and 20 characters
    private static final String usernameRegex = "^[a-zA-Z0-9_-]{8,20}$";

    @Override
    public void initialize(AuthRequestValidUsername constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(AuthRequest authRequest, ConstraintValidatorContext constraintValidatorContext) {
        String username = authRequest.getUsername();

        if(username == null || username.isEmpty()) {
            return false;
        }

        return username.matches(usernameRegex);
    }
}
