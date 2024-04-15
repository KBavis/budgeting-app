package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidName;
import com.bavis.budgetapp.request.AuthRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
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

        boolean validName = name.matches(nameRegex);
        log.debug("AuthRequest Name Passed In Validity: {}", validName);
        return validName;
    }
}