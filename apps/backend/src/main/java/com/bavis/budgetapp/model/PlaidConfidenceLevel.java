package com.bavis.budgetapp.model;

import lombok.Getter;

@Getter
public enum PlaidConfidenceLevel {
    VERY_HIGH("VERY_HIGH"),
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW"),
    UNKNOWN("UNKNOWN");

    private String level;

    PlaidConfidenceLevel(String level) {
        this.level = level;
    }
}
