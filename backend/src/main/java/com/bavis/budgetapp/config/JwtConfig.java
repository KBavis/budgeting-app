package com.bavis.budgetapp.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author Kellen Bavis
 *
 * Configuration for our JWT Implementation
 */
@Configuration("jwtConfig")
public class JwtConfig {

    private final RSAPublicKey rsaPublicKey;
    private final RSAPrivateKey rsaPrivateKey;


    /**
     * Generate RSA Key Pair to be utilized in validating/signing JWT Tokens
     */
    public JwtConfig() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            this.rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        } catch (Exception ex) {
            throw new RuntimeException("Failed to create JWT Algorithm", ex);
        }
    }

    /**
     * Used to inject Algorithm into our JwtAuthFilter & JwtService
     *
     * @return
     *      - Algorithm used to generate and validate our JWT Token
     */
    @Bean
    public Algorithm jwtAlgorithm() {
        return Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
    }
}
