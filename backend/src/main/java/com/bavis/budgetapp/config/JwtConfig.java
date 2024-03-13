package com.bavis.budgetapp.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    /**
     * Used to inject Algorithim into our JwtAuthFilter & JwtService
     *
     * @return
     *      - Algorithm used to generate and validate our JWT Token
     */
    @Bean
    public Algorithm generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
            return Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create JWT Algorithm", ex);
        }
    }
}
