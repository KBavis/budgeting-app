package com.bavis.budgetapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Kellen Bavis
 *
 * Response DTO encapsulating Category entity data merged with point-in-time VT attributes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {
    private Long categoryId;
    private String name;
    private double budgetAllocationPercentage;
    private double budgetAmount;
    private Long categoryTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
}
