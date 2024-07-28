package com.bavis.budgetapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetPerformanceId implements Serializable {
    private MonthYear monthYear;
    private Long userId;
}
