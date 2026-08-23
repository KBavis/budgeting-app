package com.bavis.budgetapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Kellen Bavis
 *
 * Response DTO for Venmo automation settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenmoAutomationDto {
    private Long automationId;
    private String ingestEmail;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastProcessedAt;
    private int enrichedCount;
    private String verificationLink;
    private boolean verified;
}
