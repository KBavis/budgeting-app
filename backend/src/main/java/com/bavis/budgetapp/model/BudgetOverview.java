package com.bavis.budgetapp.model;

import com.bavis.budgetapp.constants.OverviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetOverview {
    private OverviewType overviewType;
    private double totalSpent;
    private double totalAmountAllocated;
    private double totalPercentUtilized;
}
