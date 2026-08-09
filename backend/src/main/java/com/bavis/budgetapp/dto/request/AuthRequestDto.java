package com.bavis.budgetapp.dto.request;

import com.bavis.budgetapp.annotation.AuthRequestDuplicateUsername;
import com.bavis.budgetapp.annotation.AuthRequestSamePasswords;
import com.bavis.budgetapp.annotation.AuthRequestValidName;
import com.bavis.budgetapp.annotation.AuthRequestValidPassword;
import com.bavis.budgetapp.annotation.AuthRequestValidUsername;
import com.bavis.budgetapp.validator.group.AuthRequestAuthenticationValidationGroup;
import com.bavis.budgetapp.validator.group.AuthRequestRegistrationValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Kellen Bavis
 *
 * DTO for encapsulating needed information for either registering/authenticating user
 */
@AuthRequestSamePasswords(groups = AuthRequestRegistrationValidationGroup.class)
@AuthRequestDuplicateUsername(groups = AuthRequestRegistrationValidationGroup.class)
@AuthRequestValidUsername(groups = {AuthRequestRegistrationValidationGroup.class, AuthRequestAuthenticationValidationGroup.class})
@AuthRequestValidName(groups = AuthRequestRegistrationValidationGroup.class)
@AuthRequestValidPassword(groups = {AuthRequestRegistrationValidationGroup.class, AuthRequestAuthenticationValidationGroup.class})
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AuthRequestDto {
    private String name;
    private String username;
    private String passwordOne;
    private String passwordTwo; //validate same password
}
