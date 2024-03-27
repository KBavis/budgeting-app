package com.bavis.budgetapp.service;


import com.bavis.budgetapp.exception.BadAuthenticationRequest;
import com.bavis.budgetapp.exception.BadRegistrationRequestException;
import com.bavis.budgetapp.exception.UsernameTakenException;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;
import org.springframework.security.core.AuthenticationException;


public interface AuthService {
    AuthResponse register(AuthRequest authRequest) throws BadRegistrationRequestException, UsernameTakenException;

    AuthResponse authenticate(AuthRequest authRequest) throws AuthenticationException, BadAuthenticationRequest;

    AuthResponse refresh(AuthRequest authRequest);

    AuthResponse logout();
}
