package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.service.VenmoEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kellen Bavis
 *
 * Controller to receive inbound email webhooks from Mailgun.
 * This endpoint is NOT authenticated via JWT — Mailgun calls it directly.
 * Security is enforced via HMAC-SHA256 signature verification.
 */
@RestController
@RequestMapping("/api/webhooks")
@Log4j2
@RequiredArgsConstructor
public class VenmoWebhookController {

    private final VenmoEmailService venmoEmailService;

    /**
     * Mailgun inbound email webhook endpoint.
     * Mailgun POSTs parsed email data as multipart/form-data.
     *
     * @param recipient  - the recipient email address (contains the ingest token)
     * @param from       - the sender email address
     * @param subject    - email subject line
     * @param bodyPlain  - plain text body
     * @param bodyHtml   - HTML body (may be absent)
     * @param timestamp  - Mailgun signature timestamp
     * @param token      - Mailgun signature token
     * @param signature  - Mailgun HMAC-SHA256 signature
     * @return           - 200 OK on success, 403 on invalid signature
     */
    @PostMapping(value = "/venmo-email", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    })
    public ResponseEntity<Void> handleVenmoEmail(
            @RequestParam("recipient") String recipient,
            @RequestParam("from") String from,
            @RequestParam("subject") String subject,
            @RequestParam(value = "body-plain", required = false, defaultValue = "") String bodyPlain,
            @RequestParam(value = "body-html", required = false, defaultValue = "") String bodyHtml,
            @RequestParam(value = "timestamp", required = false, defaultValue = "") String timestamp,
            @RequestParam(value = "token", required = false, defaultValue = "") String token,
            @RequestParam(value = "signature", required = false, defaultValue = "") String signature) {

        log.info("Received Mailgun webhook — recipient: {}, from: {}, subject: {}", recipient, from, subject);

        // Verify Mailgun webhook signature
        if (!venmoEmailService.verifyMailgunSignature(timestamp, token, signature)) {
            log.warn("Invalid Mailgun webhook signature — rejecting request");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Process the email asynchronously (respond quickly to Mailgun)
        try {
            venmoEmailService.processInboundEmail(recipient, from, subject, bodyPlain, bodyHtml);
        } catch (Exception e) {
            log.error("Error processing Venmo email webhook: {}", e.getMessage(), e);
            // Still return 200 to prevent Mailgun from retrying
        }

        return ResponseEntity.ok().build();
    }
}
