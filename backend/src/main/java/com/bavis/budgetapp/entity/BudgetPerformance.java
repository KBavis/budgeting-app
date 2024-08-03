package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.BudgetPerformanceId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetPerformance {
    @EmbeddedId
    @Column(name = "BUDGET_PERFORMANCE_ID", nullable = false)
    private BudgetPerformanceId id;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "overviewType", column = @Column(name = "GENERAL_OVERVIEW_TYPE")),
            @AttributeOverride(name = "totalSpent", column = @Column(name = "GENERAL_TOTAL_SPENT")),
            @AttributeOverride(name = "totalAmountAllocated", column = @Column(name = "GENERAL_TOTAL_AMOUNT_ALLOCATED")),
            @AttributeOverride(name = "totalPercentUtilized", column = @Column(name = "GENERAL_TOTAL_PERCENT_UTILIZED")),
            @AttributeOverride(name = "totalAmountSaved", column = @Column(name = "GENERAL_TOTAL_AMOUNT_SAVED")),
            @AttributeOverride(name = "savedAmountAttributesTotal", column = @Column(name = "GENERAL_SAVED_AMOUNT_ATTRIBUTES_TOTAL"))
    })
    private BudgetOverview generalOverview;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "overviewType", column = @Column(name = "NEEDS_OVERVIEW_TYPE")),
            @AttributeOverride(name = "totalSpent", column = @Column(name = "NEEDS_TOTAL_SPENT")),
            @AttributeOverride(name = "totalAmountAllocated", column = @Column(name = "NEEDS_TOTAL_AMOUNT_ALLOCATED")),
            @AttributeOverride(name = "totalPercentUtilized", column = @Column(name = "NEEDS_TOTAL_PERCENT_UTILIZED")),
            @AttributeOverride(name = "totalAmountSaved", column = @Column(name = "NEEDS_TOTAL_AMOUNT_SAVED")),
            @AttributeOverride(name = "savedAmountAttributesTotal", column = @Column(name = "NEEDS_SAVED_AMOUNT_ATTRIBUTES_TOTAL"))
    })
    private BudgetOverview needsOverview;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "overviewType", column = @Column(name = "WANTS_OVERVIEW_TYPE")),
            @AttributeOverride(name = "totalSpent", column = @Column(name = "WANTS_TOTAL_SPENT")),
            @AttributeOverride(name = "totalAmountAllocated", column = @Column(name = "WANTS_TOTAL_AMOUNT_ALLOCATED")),
            @AttributeOverride(name = "totalPercentUtilized", column = @Column(name = "WANTS_TOTAL_PERCENT_UTILIZED")),
            @AttributeOverride(name = "totalAmountSaved", column = @Column(name = "WANTS_TOTAL_AMOUNT_SAVED")),
            @AttributeOverride(name = "savedAmountAttributesTotal", column = @Column(name = "WANTS_SAVED_AMOUNT_ATTRIBUTES_TOTAL"))
    })
    private BudgetOverview wantsOverview;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "overviewType", column = @Column(name = "INVESTMENTS_OVERVIEW_TYPE")),
            @AttributeOverride(name = "totalSpent", column = @Column(name = "INVESTMENTS_TOTAL_SPENT")),
            @AttributeOverride(name = "totalAmountAllocated", column = @Column(name = "INVESTMENTS_TOTAL_AMOUNT_ALLOCATED")),
            @AttributeOverride(name = "totalPercentUtilized", column = @Column(name = "INVESTMENTS_TOTAL_PERCENT_UTILIZED")),
            @AttributeOverride(name = "totalAmountSaved", column = @Column(name = "INVESTMENTS_TOTAL_AMOUNT_SAVED")),
            @AttributeOverride(name = "savedAmountAttributesTotal", column = @Column(name = "INVESTMENTS_SAVED_AMOUNT_ATTRIBUTES_TOTAL"))
    })
    private BudgetOverview investmentOverview;
}
