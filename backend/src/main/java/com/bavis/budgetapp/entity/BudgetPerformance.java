package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.BudgetPerformanceId;
import com.bavis.budgetapp.model.MonthYear;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(BudgetPerformanceId.class)
public class BudgetPerformance {
    @Id
    @Embedded
    private MonthYear monthYear;

    @Id
    @ManyToOne
    private User user;

    private BudgetOverview generalOverview;
    private BudgetOverview needsOverview;
    private BudgetOverview wantsOverview;
    private BudgetOverview investmentOverview;
    private CategoryType categoryType;
}