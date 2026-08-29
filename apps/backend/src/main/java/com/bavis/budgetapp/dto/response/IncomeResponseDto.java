package com.bavis.budgetapp.dto.response;

import com.bavis.budgetapp.constants.IncomeSource;
import com.bavis.budgetapp.constants.IncomeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO encapsulating Income entity data merged with point-in-time VT attributes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponseDto {
    private Long incomeId;
    private double amount;
    private IncomeType incomeType;
    private IncomeSource incomeSource;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime updatedAt;
}
