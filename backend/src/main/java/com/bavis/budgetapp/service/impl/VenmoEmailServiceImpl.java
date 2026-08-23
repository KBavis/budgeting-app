package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.config.MailgunConfig;
import com.bavis.budgetapp.dao.StagedVenmoPaymentRepository;
import com.bavis.budgetapp.dao.VenmoAutomationRepository;
import com.bavis.budgetapp.dto.response.VenmoAutomationDto;
import com.bavis.budgetapp.entity.StagedVenmoPayment;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.entity.VenmoAutomation;
import com.bavis.budgetapp.service.VenmoEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kellen Bavis
 *
 *         Implementation of the VenmoEmailService.
 *         Handles parsing Venmo notification emails, resolving users from
 *         ingest tokens, matching transactions, and enriching descriptions.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class VenmoEmailServiceImpl implements VenmoEmailService {

    private final MailgunConfig mailgunConfig;
    private final VenmoAutomationRepository venmoAutomationRepository;
    private final StagedVenmoPaymentRepository stagedVenmoPaymentRepository;
    private final UserServiceImpl userService;

    /**
     * Venmo email subject pattern for outgoing payments only.
     * Format: "You paid [Name] $XX.XX"
     *
     * We only process outgoing payments since those are the expenses
     * that need categorization. Incoming payments are not expenses.
     */
    private static final Pattern SUBJECT_YOU_PAID = Pattern.compile(
            "You paid (.+?) \\$([\\d,]+\\.\\d{2})", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern to extract the note/description from the plain-text email body.
     * Venmo emails typically include the note after a dash or in a clearly
     * delimited section. We try multiple patterns defensively.
     */
    private static final Pattern NOTE_PATTERN_QUOTED = Pattern.compile(
            "[\"\\u201C](.+?)[\"\\u201D]", Pattern.DOTALL);

    @Override
    public boolean verifyMailgunSignature(String timestamp, String token, String signature) {
        if (mailgunConfig.getApiKey() == null || mailgunConfig.getApiKey().isBlank()) {
            log.warn("Mailgun API key is not configured — skipping signature verification");
            return true; // Allow in development; should be strict in production
        }

        try {
            String data = timestamp + token;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(mailgunConfig.getApiKey().getBytes(), "HmacSHA256"));
            String computed = HexFormat.of().formatHex(mac.doFinal(data.getBytes()));
            boolean valid = computed.equalsIgnoreCase(signature);
            if (!valid) {
                log.warn("Mailgun signature verification failed. Expected: {}, Got: {}", computed, signature);
            }
            return valid;
        } catch (Exception e) {
            log.error("Error verifying Mailgun signature: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void processInboundEmail(String recipient, String from, String subject, String bodyPlain, String bodyHtml) {
        log.info("Processing inbound email — recipient: {}, from: {}, subject: {}", recipient, from, subject);

        // 1. Extract ingest token from recipient address
        String ingestToken = extractIngestToken(recipient);
        if (ingestToken == null) {
            log.warn("Could not extract ingest token from recipient: {}", recipient);
            return;
        }

        // 2. Resolve user from ingest token
        Optional<VenmoAutomation> automationOpt = venmoAutomationRepository.findByIngestToken(ingestToken);
        if (automationOpt.isEmpty()) {
            log.warn("No VenmoAutomation found for ingest token: {}", ingestToken);
            return;
        }

        VenmoAutomation automation = automationOpt.get();
        if (!automation.isEnabled()) {
            log.info("Venmo automation is disabled for user ID: {}", automation.getUser().getUserId());
            return;
        }

        // 3. Handle Gmail Auto-Forwarding Verification Email
        if (from != null && (from.toLowerCase().contains("google.com")
                || (subject != null && subject.toLowerCase().contains("gmail forwarding")))) {
            log.info("Received Google Gmail Forwarding Verification Email for user ID: {}",
                    automation.getUser().getUserId());
            handleGmailVerificationEmail(automation, bodyPlain, bodyHtml);
            return;
        }

        // 4. Validate sender is Venmo
        if (from == null || !from.toLowerCase().contains("@venmo.com")) {
            log.info("Ignoring non-Venmo/non-Google email from: {}", from);
            return;
        }

        User user = automation.getUser();
        log.info("Resolved Venmo email to user ID: {} ({})", user.getUserId(), user.getUsername());

        // 4. Parse the email
        ParsedVenmoEmail parsed = parseEmail(subject, bodyPlain, bodyHtml);
        if (parsed == null) {
            log.warn("Could not parse Venmo email — subject: {}", subject);
            return;
        }
        log.info("Parsed Venmo email — counterparty: {}, amount: {}, description: {}",
                parsed.counterparty, parsed.amount, parsed.description);

        // 5. Stage the parsed email in DB queue
        StagedVenmoPayment staged = StagedVenmoPayment.builder()
                .user(user)
                .amount(parsed.amount)
                .counterparty(parsed.counterparty)
                .description(parsed.description)
                .enrichedName(parsed.enrichedName)
                .emailTimestamp(LocalDateTime.now())
                .matched(false)
                .build();
        stagedVenmoPaymentRepository.save(staged);
        log.info("Staged Venmo payment for user ID: {} — amount: ${}, counterparty: {}",
                user.getUserId(), parsed.amount, parsed.counterparty);

        // 6. Update automation metadata
        automation.setLastProcessedAt(LocalDateTime.now());
        venmoAutomationRepository.save(automation);
    }

    @Override
    @Transactional
    public VenmoAutomationDto enableAutomation() {
        User currentUser = userService.getCurrentAuthUser();
        Long userId = currentUser.getUserId();

        // Check if already exists
        Optional<VenmoAutomation> existing = venmoAutomationRepository.findByUserUserId(userId);
        if (existing.isPresent()) {
            VenmoAutomation automation = existing.get();
            automation.setEnabled(true);
            venmoAutomationRepository.save(automation);
            log.info("Re-enabled Venmo automation for user ID: {}", userId);
            return toDto(automation);
        }

        // Create new
        VenmoAutomation automation = VenmoAutomation.builder()
                .user(currentUser)
                .build();
        automation = venmoAutomationRepository.save(automation);
        log.info("Created Venmo automation for user ID: {} with token: {}", userId, automation.getIngestToken());
        return toDto(automation);
    }

    @Override
    @Transactional
    public void disableAutomation() {
        User currentUser = userService.getCurrentAuthUser();
        Optional<VenmoAutomation> existing = venmoAutomationRepository.findByUserUserId(currentUser.getUserId());
        if (existing.isPresent()) {
            VenmoAutomation automation = existing.get();
            automation.setEnabled(false);
            venmoAutomationRepository.save(automation);
            log.info("Disabled Venmo automation for user ID: {}", currentUser.getUserId());
        }
    }

    @Override
    public VenmoAutomationDto getAutomationSettings() {
        User currentUser = userService.getCurrentAuthUser();
        return venmoAutomationRepository.findByUserUserId(currentUser.getUserId())
                .map(this::toDto)
                .orElse(null);
    }

    // ==================== Private Helpers ====================

    /**
     * Extract the ingest token from a recipient address.
     * Expected format: venmo-{token}@mail.bavisbudgeting.com
     */
    String extractIngestToken(String recipient) {
        if (recipient == null || recipient.isBlank())
            return null;

        // Handle formats like "venmo-{token}@domain" or "<venmo-{token}@domain>"
        String cleaned = recipient.replaceAll("[<>]", "").trim().toLowerCase();
        int atIndex = cleaned.indexOf('@');
        if (atIndex < 0)
            return null;

        String localPart = cleaned.substring(0, atIndex);
        if (!localPart.startsWith("venmo-"))
            return null;

        String token = localPart.substring("venmo-".length());
        return token.isBlank() ? null : token;
    }

    /**
     * Parse a Venmo "You paid" email notification to extract structured data.
     * Only processes outgoing payments — incoming payments are ignored
     * since they are not expenses that need categorization.
     */
    ParsedVenmoEmail parseEmail(String subject, String bodyPlain, String bodyHtml) {
        if (subject == null || subject.isBlank())
            return null;

        // Only match outgoing payment subjects
        Matcher m = SUBJECT_YOU_PAID.matcher(subject);
        if (!m.find()) {
            log.debug("Subject does not match 'You paid' pattern (ignoring non-outgoing): {}", subject);
            return null;
        }

        String counterparty = m.group(1).trim();
        double amount = parseAmount(m.group(2));

        // Extract note/description from body
        String description = extractDescription(bodyPlain, bodyHtml);

        // Build enriched transaction name
        String enrichedName;
        if (description != null && !description.isBlank()) {
            enrichedName = description + " (" + counterparty + ")";
        } else {
            enrichedName = "Venmo - " + counterparty;
        }

        return new ParsedVenmoEmail(counterparty, amount, description, enrichedName);
    }

    /**
     * Extract the Venmo note/description from the email body.
     * In Venmo emails, the note is deterministically placed on the line immediately
     * following the amount line and preceding the "See transaction" section.
     */
    String extractDescription(String bodyPlain, String bodyHtml) {
        if (bodyPlain == null || bodyPlain.isBlank()) {
            return null;
        }

        String[] lines = bodyPlain.split("\\r?\\n");
        boolean afterAmount = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            // 1. Find the amount line (e.g. "$20.00" or "$20")
            if (!afterAmount && (trimmed.matches("^\\$[0-9.,]+$") || trimmed.startsWith("$"))) {
                afterAmount = true;
                continue;
            }

            // 2. The next non-empty line after amount is the note
            if (afterAmount) {
                String lower = trimmed.toLowerCase();
                // Stop if there is no note (hits system button/header)
                if (lower.contains("see transaction") || lower.contains("view transaction")
                        || lower.contains("transaction details") || lower.startsWith("http")
                        || lower.startsWith("date") || lower.startsWith("status")) {
                    break;
                }
                return trimmed;
            }
        }

        return null;
    }

    /**
     * Parse an amount string like "25.00" or "1,250.00" to a double.
     */
    private double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr.replace(",", ""));
        } catch (NumberFormatException e) {
            log.warn("Could not parse amount: {}", amountStr);
            return 0;
        }
    }

    @Override
    @Transactional
    public VenmoAutomationDto verifyAutomation() {
        User currentUser = userService.getCurrentAuthUser();
        Optional<VenmoAutomation> existing = venmoAutomationRepository.findByUserUserId(currentUser.getUserId());
        if (existing.isPresent()) {
            VenmoAutomation automation = existing.get();
            automation.setVerified(true);
            venmoAutomationRepository.save(automation);
            log.info("Marked Venmo automation verified for user ID: {}", currentUser.getUserId());
            return toDto(automation);
        }
        return null;
    }

    /**
     * Parse and store Gmail forwarding confirmation link from Google's automated email.
     */
    private void handleGmailVerificationEmail(VenmoAutomation automation, String bodyPlain, String bodyHtml) {
        String content = (bodyPlain != null ? bodyPlain : "") + "\n" + (bodyHtml != null ? bodyHtml : "");

        // Gmail Confirmation Link (e.g. https://mail-settings.google.com/mail/vf-...)
        Pattern linkPattern = Pattern.compile("(https://mail-settings\\.google\\.com/[^\\s\"'>]+)");
        Matcher linkMatcher = linkPattern.matcher(content);
        if (linkMatcher.find()) {
            automation.setVerificationLink(linkMatcher.group(1));
            log.info("Extracted Gmail verification link for user ID {}", automation.getUser().getUserId());
        }

        venmoAutomationRepository.save(automation);
    }

    /**
     * Convert a VenmoAutomation entity to a response DTO.
     */
    private VenmoAutomationDto toDto(VenmoAutomation automation) {
        String ingestEmail = "venmo-" + automation.getIngestToken() + "@" + mailgunConfig.getIngestDomain();
        return VenmoAutomationDto.builder()
                .automationId(automation.getAutomationId())
                .ingestEmail(ingestEmail)
                .enabled(automation.isEnabled())
                .createdAt(automation.getCreatedAt())
                .lastProcessedAt(automation.getLastProcessedAt())
                .enrichedCount(automation.getEnrichedCount())
                .verificationLink(automation.getVerificationLink())
                .verified(automation.isVerified())
                .build();
    }

    /**
     * Internal record holding parsed Venmo email data.
     * Only represents outgoing payments ("You paid").
     */
    record ParsedVenmoEmail(
            String counterparty,
            double amount,
            String description,
            String enrichedName) {
    }
}
