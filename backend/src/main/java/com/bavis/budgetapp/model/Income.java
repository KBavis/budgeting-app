package com.bavis.budgetapp.model;


import com.bavis.budgetapp.enumeration.IncomeSource;
import com.bavis.budgetapp.enumeration.IncomeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
