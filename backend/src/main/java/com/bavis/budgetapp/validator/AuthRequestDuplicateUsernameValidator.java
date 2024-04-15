package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestDuplicateUsername;
import com.bavis.budgetapp.annotation.AuthRequestValidUsername;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
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

        boolean userExists = userService.existsByUsername(username);
        log.debug("AuthRequest Username Already Doesn't Exist: {}", !userExists);

        //note: existsByUsername returns false if user DNE, meaning it's valid
        return !userExists;
    }
}