package com.bavis.budgetapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kellen Bavis
 *
 * Configuration for Mailgun inbound email processing.
 * The API key is used for webhook signature verification (HMAC-SHA256).
 */
@Configuration
@Getter
public class MailgunConfig {

    @Value("${mailgun.api-key:}")
    private String apiKey;

    @Value("${mailgun.ingest-domain:mail.bavisbudgeting.com}")
    private String ingestDomain;
}
