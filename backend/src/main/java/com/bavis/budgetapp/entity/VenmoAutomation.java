package com.bavis.budgetapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
     * Confirmation code extracted from Google's forwarding verification email (if using Gmail).
     */
    @Column
    private String verificationCode;

    /**
     * Direct verification URL extracted from Google's forwarding verification email.
     */
    @Column(columnDefinition = "TEXT")
    private String verificationLink;
}
