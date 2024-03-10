package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;
import com.bavis.budgetapp.service.AuthService;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final JwtService _jwtService;
    private final UserService _userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager _authenticationManager;

    public AuthServiceImpl(JwtService _jwtService, UserService _userService, PasswordEncoder _passwordEncoder, AuthenticationManager _authenticationManager){
        this._jwtService = _jwtService;
        this._userService = _userService;
        this._authenticationManager = _authenticationManager;
        this.passwordEncoder = _passwordEncoder;
    }


    @Override
    public AuthResponse register(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse refresh(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse logout() {
        return null;
    }
}
