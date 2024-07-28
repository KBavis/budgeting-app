package com.bavis.budgetapp.constants;

import lombok.Getter;

/**
 * Enum of
 */
@Getter
public enum OverviewType {

    NEEDS("Needs"),
    WANTS("Wants"),
    INVESTMENTS("Investments"),
    GENERAL("General");

    private final String type;
    OverviewType(String type){
        this.type = type;
    }
}
