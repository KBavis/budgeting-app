package com.bavis.budgetapp.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

public interface JwtService {
    boolean validateToken(DecodedJWT decodedJWT, UserDetails userDetails);

    String generateToken(User user) throws RuntimeException;
}

