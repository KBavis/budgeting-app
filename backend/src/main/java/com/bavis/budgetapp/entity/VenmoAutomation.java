package com.bavis.budgetapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Kellen Bavis
 *
 * Entity storing per-user Venmo email automation configuration.
 * Each user receives a unique ingest token that forms their personalized
 * email address for forwarding Venmo notifications.
 */
@Entity
@Table(name = "venmo_automation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenmoAutomation {

    /**
     * Tracks which phase of the two-phase setup the user is currently in.
     *
     * FORWARDING_VERIFICATION — User must verify the forwarding address with their
     *                           email provider (e.g., Gmail sends a confirmation email).
     *                           Providers that don't require this step skip directly
     *                           to FILTER_SETUP.
     * FILTER_SETUP             — User must create a filter/rule in their email client
     *                           to forward emails with subject "You paid" from Venmo.
     * COMPLETE                 — Both phases are done; automation is fully operational.
     */
    public enum SetupPhase {
        FORWARDING_VERIFICATION,
        FILTER_SETUP,
        COMPLETE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long automationId;

    @ToString.Exclude
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", unique = true, nullable = false)
    private User user;

    /**
     * Unique token used to construct the user's ingest email address.
     * Format: venmo-{ingestToken}@mail.bavisbudgeting.com
     */
    @Builder.Default
    @Column(nullable = false, unique = true, updatable = false)
    private String ingestToken = UUID.randomUUID().toString();

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime lastProcessedAt;

    /**
     * Tracks the total number of Venmo transactions successfully enriched.
     */
    @Builder.Default
    @Column(nullable = false)
    private int enrichedCount = 0;

    /**
     * Direct verification URL extracted from Google's forwarding verification email.
     */
    @Column(columnDefinition = "TEXT")
    private String verificationLink;

    /**
     * The user's email provider, used to determine which setup phases are required.
     * Examples: "GMAIL", "OUTLOOK", "YAHOO", "OTHER".
     * Providers like Gmail require forwarding verification; others may not.
     */
    @Column(length = 50)
    private String emailProvider;

    /**
     * Current phase of the two-phase setup process.
     * Defaults to FORWARDING_VERIFICATION for providers that require it.
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SetupPhase setupPhase = SetupPhase.FORWARDING_VERIFICATION;
}
