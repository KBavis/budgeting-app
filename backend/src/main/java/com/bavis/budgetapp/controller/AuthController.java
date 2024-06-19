package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.AuthService;
import com.bavis.budgetapp.service.impl.AuthServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.bavis.budgetapp.validator.group.AuthRequestAuthenticationValidationGroup;
import com.bavis.budgetapp.validator.group.AuthRequestRegistrationValidationGroup;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kellen Bavis
 *
 * Controller class to deal with JWT Token Creation/Deletion, Authenticating Users, And Registering Users
 *
 */
@RestController
@Log4j2
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final AuthServiceImpl authService;
    private final UserServiceImpl userService;

    public AuthController(AuthServiceImpl _authService, UserServiceImpl _userService){
        this.authService = _authService;
        this.userService = _userService;
    }

    /**
     * Registers a new User to our application
     *
     * @param authRequestDto
     *          - Request information needed for registering a new user
     * @return
     *          - Newly register User and created JWT Token for subsequent requests
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Validated(AuthRequestRegistrationValidationGroup.class) @RequestBody AuthRequestDto authRequestDto) {
        log.info("Received request to register a new User: [{}}", authRequestDto);
        return ResponseEntity.ok(authService.register(authRequestDto));
    }


    /**
     * Fetch current Authenticated user
     *
     * @return
     *      - Authenticated user
     */
    @GetMapping
    public ResponseEntity<User> getAuthenticatedUser() {
        log.info("Received request to fetch current authenticated User");
        return ResponseEntity.ok(userService.getCurrentAuthUser());
    }

    /**
     * Authenticates an existing User
     *
     * @param authRequestDto
     *          - Request information needed for authenticating a particular user
     * @return
     *          - Authenticated user and created JWT Token for subsequent requests
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(@Validated(AuthRequestAuthenticationValidationGroup.class) @RequestBody AuthRequestDto authRequestDto) {
        log.info("Received request to authenticate an User: [{}]", authRequestDto);
        return ResponseEntity.ok(authService.authenticate(authRequestDto));
    }

    /**
     * Refreshes the expiration date of our JWT Token
     *
     * TODO: Implement this logic
     *
     * @param authRequestDto
     * @return
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody AuthRequestDto authRequestDto) {
        return null;
    }

    /**
     * Logs a User out of their currently logged in Account
     *
     */
    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }


}
