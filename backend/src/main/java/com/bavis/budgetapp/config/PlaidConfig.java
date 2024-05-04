package com.bavis.budgetapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * @author Kellen Bavis
 *
 * Configuration class utilized to house our Plaid Client values from external properties file
 */
@Configuration("plaidConfig")
@Getter
public class PlaidConfig {

    @Value("${plaid.api.client-id}")
    private String clientId;

    @Value("${plaid.api.secret-key}")
    private String secretKey;

}
