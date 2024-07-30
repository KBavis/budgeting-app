package com.bavis.budgetapp.model;

import com.bavis.budgetapp.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@EqualsAndHashCode
public class BudgetPerformanceId implements Serializable {
    private MonthYear monthYear;
    private Long userId;
}
