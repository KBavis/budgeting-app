package com.bavis.budgetapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PlaidConfig {

    @Value("${plaid.api.client-id}")
    private String clientId;

    @Value("${plaid.api.secret-key}")
    private String secretKey;

    @Bean
    public Decoder accessTokenDecoder(ObjectMapper objectMapper) {
        return new CustomAccessTokenDecoder(objectMapper);
    }
}
