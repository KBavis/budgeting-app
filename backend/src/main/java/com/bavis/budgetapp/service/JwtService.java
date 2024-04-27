package com.bavis.budgetapp.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    boolean validateToken(DecodedJWT decodedJWT, UserDetails userDetails);

    String generateToken(User user) throws RuntimeException;
}

