package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import com.bavis.budgetapp.service.AuthService;
import com.bavis.budgetapp.validator.group.AuthRequestAuthenticationValidationGroup;
import com.bavis.budgetapp.validator.group.AuthRequestRegistrationValidationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to deal with JWT Token Creation/Deletion, Authenticating Users, And Registering Users
 *
 * TODO: Finalize Implementation of Authentication and Utilize Authentication Service
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final AuthService _authService;

    public AuthController(AuthService _authService){
        this._authService = _authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Validated(AuthRequestRegistrationValidationGroup.class) @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(_authService.register(authRequestDto));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(@Validated(AuthRequestAuthenticationValidationGroup.class) @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(_authService.authenticate(authRequestDto));
    }

    /**
     * Refreshes the expiration date of our JWT Token
     *
     * TODO: Determine how to implement this logic
     *
     * @param authRequestDto
     * @return
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody AuthRequestDto authRequestDto) {
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDto> logout() {
        return null;
    }


}
