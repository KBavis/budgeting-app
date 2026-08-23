package com.bavis.budgetapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * @author Kellen Bavis
 *
 * Entity storing staged Venmo payment details extracted from emails.
 * These staged payments wait in a queue until Plaid syncs the corresponding
 * bank transaction, at which point the transaction name is enriched.
 */
@Entity
@Table(name = "staged_venmo_payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StagedVenmoPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stagedId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String counterparty;

    @Column
    private String description;

    @Column(nullable = false)
    private String enrichedName;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime emailTimestamp = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    private boolean matched = false;

    @Column
    private LocalDateTime matchedAt;
}
