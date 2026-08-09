package com.bavis.budgetapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
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
 * Valid Time (VT) Entity for storing Category state history over time
 */
@Entity
@Table(name = "category_vt")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVt extends ValidTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    private String name;

    private double budgetAllocationPercentage;

    private double budgetAmount;

    @ManyToOne
    @JoinColumn(name = "category_type_id")
    @JsonIgnoreProperties("categories")
    private CategoryType categoryType;

    @Override
    public void copyAttributesFrom(ValidTimeEntity source) {
        if (source instanceof CategoryVt other) {
            if (other.getName() != null) this.name = other.getName();
            this.budgetAllocationPercentage = other.getBudgetAllocationPercentage();
            this.budgetAmount = other.getBudgetAmount();
            if (other.getCategoryType() != null) this.categoryType = other.getCategoryType();
        }
    }
}
