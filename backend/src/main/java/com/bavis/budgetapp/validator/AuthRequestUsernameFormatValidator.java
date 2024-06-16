package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidUsername;
import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * @author Kellen Bavis
 *
 * Validation class for ensuring AuthRequest contains valid username
 *      - Contains only lowercase/uppercase letters, digits, underscores, or hyphens
 *      - Minimum of 6 characters long and at most 20 characters long
 */
public class AuthRequestUsernameFormatValidator implements ConstraintValidator<AuthRequestValidUsername, AuthRequestDto> {

    //Validates all characters are a letter ,digit, underscore, or hyphen AND is between 6 and 20 characters
    private static final String usernameRegex = "^[a-zA-Z0-9._-]{6,20}$";

    @Override
    public void initialize(AuthRequestValidUsername constraintAnnotation) {
        //nothing to initalize
    }

    /**
     * Validates the passed in 'username' attribute format for our AuthRequestDto
     *
     * @param authRequestDto
     *          - AuthRequestDto to validate 'username' attribute for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the 'username' attribute in the AuthRequestDto
     */
    @Override
    public boolean isValid(AuthRequestDto authRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String username = authRequestDto.getUsername();

        if(username == null || username.isEmpty()) {
            return false;
        }

        return username.matches(usernameRegex);
    }
}
