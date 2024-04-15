package com.bavis.budgetapp.service;


import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;
import org.springframework.security.core.AuthenticationException;


public interface AuthService {
    AuthResponse register(AuthRequest authRequest) throws UserServiceException, PlaidServiceException, JwtServiceException;

    AuthResponse authenticate(AuthRequest authRequest) throws AuthenticationException;

    AuthResponse refresh(AuthRequest authRequest);

    AuthResponse logout();
}
