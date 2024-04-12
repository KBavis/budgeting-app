package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestDuplicateUsername;
import com.bavis.budgetapp.annotation.AuthRequestValidUsername;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthRequestDuplicateUsernameValidator implements ConstraintValidator<AuthRequestDuplicateUsername, AuthRequest> {

    @Autowired
    UserService userService;


    @Override
    public void initialize(AuthRequestDuplicateUsername constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(AuthRequest authRequest, ConstraintValidatorContext constraintValidatorContext) {
        String username = authRequest.getUsername();

        if(username == null || username.isEmpty()) {
            return false;
        }

        return userService.existsByUsername(username);
    }
}
