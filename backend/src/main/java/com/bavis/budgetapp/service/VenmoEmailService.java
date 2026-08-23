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
     * @param emailProvider
     *          - the user's email provider (e.g., "GMAIL", "OUTLOOK", "YAHOO", "OTHER")
     * @return
     *          - the created automation settings including the ingest email address
     */
    VenmoAutomationDto enableAutomation(String emailProvider);

    /**
     * Disable Venmo automation for the currently authenticated user.
     */
    void disableAutomation();

    /**
     * Mark phase 1 (forwarding address verification) as complete.
     * Transitions setupPhase from FORWARDING_VERIFICATION to FILTER_SETUP.
     *
     * @return
     *          - updated automation settings
     */
    VenmoAutomationDto completeForwardingVerification();

    /**
     * Get the current Venmo automation settings for the authenticated user.
     *
     * @return
     *          - automation settings or null if not configured
     */
    VenmoAutomationDto getAutomationSettings();

    /**
     * Mark Phase 2 (filter/rule setup) as complete for the currently authenticated user.
     * Transitions setupPhase from FILTER_SETUP to COMPLETE.
     *
     * @return
     *          - updated automation settings with setupPhase=COMPLETE
     */
    VenmoAutomationDto completeFilterSetup();
}
