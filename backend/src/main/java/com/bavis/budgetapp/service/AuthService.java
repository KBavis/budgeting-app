package com.bavis.budgetapp.service;


import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest authRequest);

    AuthResponse authenticate(AuthRequest authRequest);

    AuthResponse refresh(AuthRequest authRequest);

    AuthResponse logout();
}
