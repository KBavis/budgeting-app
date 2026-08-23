package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.response.VenmoAutomationDto;

/**
 * @author Kellen Bavis
 *
 * Service interface for Venmo email automation functionality.
 * Handles inbound email processing and user automation management.
 */
public interface VenmoEmailService {

    /**
     * Verify a Mailgun webhook signature to ensure authenticity.
     *
     * @param timestamp
     *          - Unix timestamp from Mailgun
     * @param token
     *          - random token from Mailgun
     * @param signature
     *          - HMAC-SHA256 signature to verify
     * @return
     *          - true if the signature is valid
     */
    boolean verifyMailgunSignature(String timestamp, String token, String signature);

    /**
     * Process an inbound Venmo email notification.
     * Parses the email content, resolves the target user from the recipient address,
     * and enriches matching Venmo transactions with extracted descriptions.
     *
     * @param recipient
     *          - the full recipient email address (e.g., venmo-{token}@mail.bavisbudgeting.com)
     * @param from
     *          - the sender email address
     * @param subject
     *          - email subject line
     * @param bodyPlain
     *          - plain text email body
     * @param bodyHtml
     *          - HTML email body (may be null)
     */
    void processInboundEmail(String recipient, String from, String subject, String bodyPlain, String bodyHtml);

    /**
     * Enable Venmo automation for the currently authenticated user.
     * Creates a new VenmoAutomation record with a unique ingest token.
     *
     * @return
     *          - the created automation settings including the ingest email address
     */
    VenmoAutomationDto enableAutomation();

    /**
     * Disable Venmo automation for the currently authenticated user.
     */
    void disableAutomation();

    /**
     * Mark Venmo automation as verified for the currently authenticated user.
     *
     * @return
     *          - updated automation settings with verified=true
     */
    VenmoAutomationDto verifyAutomation();

    /**
     * Get the current Venmo automation settings for the authenticated user.
     *
     * @return
     *          - automation settings or null if not configured
     */
    VenmoAutomationDto getAutomationSettings();
}
