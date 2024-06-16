package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestDuplicateUsername;
import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Kellen Bavis
 *
 * Validation class to ensure that the 'username' passed in for our AuthRequest is unique
 *          - Username isn't already registered to corresponding user
 */
@Component
public class AuthRequestDuplicateUsernameValidator implements ConstraintValidator<AuthRequestDuplicateUsername, AuthRequestDto> {

    @Autowired
    UserService userService;


    @Override
    public void initialize(AuthRequestDuplicateUsername constraintAnnotation) {
        //nothing to initalize
    }

    /**
     * Validates that the username passed in for AuthRequestDto is unique
     *
     * @param authRequestDto
     *              - AuthRequestDto to validate username for
     * @param constraintValidatorContext
     *              - provides additional context information for our constraint
     * @return
     *              - Validity of passed in username attribute in AuthRequestDto
     */
    @Override
    public boolean isValid(AuthRequestDto authRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String username = authRequestDto.getUsername();

        if(username == null || username.isEmpty()) {
            return false;
        }

        boolean userExists = userService.existsByUsername(username);
        //note: existsByUsername returns false if user DNE, meaning it's valid
        return !userExists;
    }
}
