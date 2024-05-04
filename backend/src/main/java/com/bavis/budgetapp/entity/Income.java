package com.bavis.budgetapp.entity;


import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Kellen Bavis
 *
 * Entity for storing information regarding a user's monthly income
 */
@Entity
@Table(name = "income")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore //avoid circular reference
    private User user;

    private double amount;


    @Enumerated(EnumType.STRING)
    @JsonProperty("incomeType")
    private IncomeType incomeType;

    @Enumerated(EnumType.STRING)
    @JsonProperty("incomeSource")
    private IncomeSource incomeSource;

    private String description;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt; //last time income was updated

}
