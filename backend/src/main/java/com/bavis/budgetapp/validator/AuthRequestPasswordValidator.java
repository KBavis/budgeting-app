package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidPassword;
import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * @author Kellen Bavis
 *
 * Validation class for ensuring AuthRequest contains a valid password
 *      - At least one digit (0 - 9)
 *      - At least one alphabetical letter (a-zA-Z)
 *      - At least one special character (@#$%^&+=!)
 *      - No white space characters
 *      - Minimum length of 10 characters
 */
public class AuthRequestPasswordValidator implements ConstraintValidator<AuthRequestValidPassword, AuthRequestDto> {

    private static final String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{10,}$";

    @Override
    public void initialize(AuthRequestValidPassword constraintAnnotation) {
        //nothing to initalize
    }

    /**
     * Validates the format of the passed in 'password' for our AuthRequestDto
     *
     * @param authRequestDto
     *          - AuthRequestDto to validate 'password' for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the 'password' attribute in the AuthRequestDto
     */
    @Override
    public boolean isValid(AuthRequestDto authRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String password = authRequestDto.getPasswordOne();
        if(password == null || password.isEmpty()){
            return false;
        }

        return password.matches(passwordRegex);
    }
}
