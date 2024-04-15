package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidName;
import com.bavis.budgetapp.annotation.AuthRequestValidPassword;
import com.bavis.budgetapp.request.AuthRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthRequestPasswordValidator implements ConstraintValidator<AuthRequestValidPassword, AuthRequest> {

    /**
     * Validations:
     *      - At least one digit (0 - 9)
     *      - At least one alphabetical letter (a-zA-Z)
     *      - At least one special character (@#$%^&+=!)
     *      - No white space characters
     *      - Minimum length of 10 characters
     */
    private static final String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{10,}$";

    @Override
    public void initialize(AuthRequestValidPassword constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(AuthRequest authRequest, ConstraintValidatorContext constraintValidatorContext) {
        String password = authRequest.getPasswordOne();
        if(password == null || password.isEmpty()){
            return false;
        }

        boolean passwordValid = password.matches(passwordRegex);
        log.debug("AuthRequest Password Validity: {}", passwordValid);
        return passwordValid;
    }
}