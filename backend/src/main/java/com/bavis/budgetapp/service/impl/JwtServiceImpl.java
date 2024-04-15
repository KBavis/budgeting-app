package com.bavis.budgetapp.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.enumeration.TimeType;
import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.util.GeneralUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.DialectOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger LOG = LoggerFactory.getLogger(JwtServiceImpl.class);

    private final Algorithm _algorithm;

    public JwtServiceImpl(Algorithm _algorithm) {
        this._algorithm = _algorithm;
    }

    /**
     * Validate token is non-expired and is corresponding to the authenticated user
     * TODO: Consider where we should implement the logic regarding a users locked account
     *
     * @param decodedJWT
     *             - decoded/verified JWT token
     * @param userDetails
     *              - user details corresponding to authenticated user
     * @return
     *       - boolean determine the validity of the token
     */
    @Override
    public boolean validateToken(DecodedJWT decodedJWT, UserDetails userDetails) {
        Date expirationDate = decodedJWT.getExpiresAt();
        final String username = decodedJWT.getSubject();
        return (username.equals(userDetails.getUsername()) && expirationDate.after(new Date()));
    }
    /**
     * Generates JWT Token For the Respective User
     *
     * @param user
     *          - user being authenticated
     * @return
     *          - JWT Token
     */
    @Override
    public String generateToken(User user) throws JwtServiceException {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = GeneralUtil.addTimeToDate(currentTime,3, TimeType.HOURS);

        try {
            return JWT.create()
                    .withIssuer("bavis")
                    .withSubject(user.getUsername())
                    .withExpiresAt(GeneralUtil.localDateTimeToDate(expirationTime))
                    .sign(_algorithm);
        } catch (Exception e) {
            LOG.error("Failed to generated JWT Token for User [{}]", user.toString());
            throw new JwtServiceException("Failed to Generate JWT Token: " + e.getMessage());
        }
    }


}
