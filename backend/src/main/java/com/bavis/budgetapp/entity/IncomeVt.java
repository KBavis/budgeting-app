package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Kellen Bavis
 * 
 * Valid Time (VT) Entity for storing Income state history over time
 */
@Entity
@Table(name = "income_vt")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeVt extends ValidTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "income_id", nullable = false)
    @JsonIgnore
    private Income income;

    private double amount;

    @Enumerated(EnumType.STRING)
    private IncomeType incomeType;

    @Enumerated(EnumType.STRING)
    private IncomeSource incomeSource;

    private String description;

    @Override
    public void copyAttributesFrom(ValidTimeEntity source) {
        if (source instanceof IncomeVt other) {
            this.amount = other.getAmount();
            if (other.getIncomeType() != null) this.incomeType = other.getIncomeType();
            if (other.getIncomeSource() != null) this.incomeSource = other.getIncomeSource();
            if (other.getDescription() != null) this.description = other.getDescription();
        }
    }
}
