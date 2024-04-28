package com.bavis.budgetapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
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

    // Set the logging level
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Adjust this based on your requirement
    }

    // Register the custom logger
    @Bean
    public Logger logger() {
        return new OneLineLogger(); // Your custom logger
    }

    private static class OneLineLogger extends feign.Logger {
        @Override
        protected void log(String configKey, String format, Object... args) {
            // Format log message as a single line
            String message = String.format(configKey + " - " + format, args);
            System.out.println(message);
        }
    }

}
