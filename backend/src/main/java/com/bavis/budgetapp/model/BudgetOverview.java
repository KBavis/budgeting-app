package com.bavis.budgetapp.model;

import com.bavis.budgetapp.constants.OverviewType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class BudgetOverview {
    @Enumerated(EnumType.STRING)
    private OverviewType overviewType;

    private double totalSpent;

    private double totalAmountAllocated;

    private double totalPercentUtilized;
}
