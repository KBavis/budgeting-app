package com.bavis.budgetapp.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.JwtService;
import org.hibernate.annotations.DialectOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;


@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger LOG = LoggerFactory.getLogger(JwtServiceImpl.class);

    /**
     * Validate token is non-expired and is corresponding to the authenticated user
     * TODO: Consider where we should implement the logic regarding a users locked account
     *
     * @param decodedJWT
     *             - decoded/verified JWT token
     * @param userDetails
     *              - user details corresponding to authenticated user
     * @return
     *       - boolean determing the validity of the token
     */
    @Override
    public boolean validateToken(DecodedJWT decodedJWT, UserDetails userDetails) {
        Date expirationDate = decodedJWT.getExpiresAt();
        final String username = decodedJWT.getSignature();
        return (username.equals(userDetails.getUsername()) && expirationDate.before(new Date()));
    }

    /**
     * Generates JWT Token For the Respective User
     * TODO: Finalize this implemetnation
     *
     * @param user
     *          - user being authenticated
     * @return
     *          - JWT Token
     */
    @Override
    public String generateToken(User user){
        return null;
    }


}
