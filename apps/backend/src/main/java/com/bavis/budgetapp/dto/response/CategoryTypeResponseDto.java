package com.bavis.budgetapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Response DTO encapsulating CategoryType entity data merged with point-in-time VT attributes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTypeResponseDto {
    private Long categoryTypeId;
    private String name;
    private double budgetAllocationPercentage;
    private double budgetAmount;
    private double savedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
}
