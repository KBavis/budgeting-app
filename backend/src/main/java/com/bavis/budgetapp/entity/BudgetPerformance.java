package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.MonthYear;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetPerformance {
    private MonthYear monthYear;
    private BudgetOverview generalOverview;
    private BudgetOverview needsOverview;
    private BudgetOverview wantsOverview;
    private BudgetOverview investmentOverview;
    private User user;
    private CategoryType categoryType;
}
