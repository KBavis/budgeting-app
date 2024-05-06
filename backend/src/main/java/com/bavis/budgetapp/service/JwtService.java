package com.bavis.budgetapp.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Kellen Bavis
 *
 * Service to house functionality regarding our Jwt Tokens
 */
public interface JwtService {
    /**
     * Functionality to validate that a JWT token is valid (non-expired & corresponds to requesting User)
     *
     * @param decodedJWT
     *          - Decoded JWT Token corresponding to HTTP Request
     * @param userDetails
     *          - User corresponding to a particular HTTP Request
     * @return
     *          - Validity of specified JWT Token
     */
    boolean validateToken(DecodedJWT decodedJWT, UserDetails userDetails);

    /**
     * Functionality to generate a valid JWT Token for a particular user
     *
     * @param user
     *          - User to generate JWT Token for
     * @return
     *          - Generated JWT Token
     * @throws RuntimeException
     *          - Thrown in the case that an error occurs while generating JWT token
     */
    String generateToken(User user) throws RuntimeException;
}

