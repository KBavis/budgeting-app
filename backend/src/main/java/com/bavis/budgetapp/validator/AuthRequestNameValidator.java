package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidName;
import com.bavis.budgetapp.request.AuthRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AuthRequestNameValidator implements ConstraintValidator<AuthRequestValidName, AuthRequest> {

    //Validates proper name format
    String nameRegex = "^[A-Z][a-zA-Z'-]+(\\s[A-Z][a-zA-Z'-]+)*\\s[A-Z][a-zA-Z'-]+$";
    @Override
    public void initialize(AuthRequestValidName constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(AuthRequest authRequest, ConstraintValidatorContext constraintValidatorContext) {
        String name = authRequest.getName();

        if(name == null || name.isEmpty()) {
            return false;
        }

        return name.matches(nameRegex);
    }
}
