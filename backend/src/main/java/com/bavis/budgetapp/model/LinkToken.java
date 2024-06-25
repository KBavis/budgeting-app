package com.bavis.budgetapp.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Kellen Bavis
 *
 * Model to represent both our LinkToken and Expiration
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Embeddable
public class LinkToken {
    private LocalDateTime expiration;
    private String token;
}
