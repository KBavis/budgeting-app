package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestSamePasswords;
import com.bavis.budgetapp.request.AuthRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AuthRequestSamePasswordsValidator implements ConstraintValidator<AuthRequestSamePasswords, AuthRequest> {

    @Override
    public void initialize(AuthRequestSamePasswords constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(AuthRequest authRequest, ConstraintValidatorContext constraintValidatorContext) {
        String passwordOne = authRequest.getPasswordOne();
        String passwordTwo = authRequest.getPasswordTwo();

        //Password fields must be filled out
        if(passwordOne == null || passwordOne.isEmpty() || passwordTwo == null || passwordTwo.isEmpty()){
            return false;
        }

        return passwordTwo.equals(passwordOne);
    }
}
