package com.bavis.budgetapp.request;

import com.bavis.budgetapp.annotation.*;
import com.bavis.budgetapp.validator.group.AuthRequestAuthenticationValidationGroup;
import com.bavis.budgetapp.validator.group.AuthRequestRegistrationValidationGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@AuthRequestSamePasswords(groups = AuthRequestRegistrationValidationGroup.class)
@AuthRequestDuplicateUsername(groups = AuthRequestRegistrationValidationGroup.class)
@AuthRequestValidUsername(groups = {AuthRequestRegistrationValidationGroup.class, AuthRequestAuthenticationValidationGroup.class})
@AuthRequestValidName(groups = AuthRequestRegistrationValidationGroup.class)
@AuthRequestValidPassword(groups = {AuthRequestRegistrationValidationGroup.class, AuthRequestAuthenticationValidationGroup.class})
@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class AuthRequest {
    private final String name;
    private final String username;
    private final String passwordOne;
    private final String passwordTwo; //validate same password
}
