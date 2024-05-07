package com.bavis.budgetapp.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.constants.TimeType;
import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * @author Kellen Bavis
 *
 * Implementation of our Jwt Service functionality
 */
@Service
@Log4j2
public class JwtServiceImpl implements JwtService {
    private final Algorithm _algorithm;

    public JwtServiceImpl(Algorithm _algorithm) {
        this._algorithm = _algorithm;
    }

    @Override
    public boolean validateToken(DecodedJWT decodedJWT, UserDetails userDetails) {
        log.info("Validating JWT Token for User with username '{}'", userDetails.getUsername());
        Date expirationDate = decodedJWT.getExpiresAt();
        final String username = decodedJWT.getSubject();
        return (username.equals(userDetails.getUsername()) && expirationDate.after(new Date()));
    }

    @Override
    public String generateToken(User user) throws JwtServiceException {
        log.info("Generating JWT Token for User [{}]", user);
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = GeneralUtil.addTimeToDate(currentTime,3, TimeType.HOURS);

        try {
            return JWT.create()
                    .withIssuer("bavis")
                    .withSubject(user.getUsername())
                    .withExpiresAt(GeneralUtil.localDateTimeToDate(expirationTime))
                    .sign(_algorithm);
        } catch (Exception e) {
            log.error("Failed to generated JWT Token for User [{}]", user.toString());
            throw new JwtServiceException("Failed to Generate JWT Token: " + e.getMessage());
        }
    }


}
