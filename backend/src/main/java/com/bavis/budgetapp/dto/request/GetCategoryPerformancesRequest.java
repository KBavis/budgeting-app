package com.bavis.budgetapp.dto.request;

import com.bavis.budgetapp.model.MonthYear;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kellen Bavis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCategoryPerformancesRequest {
    private List<Long> categoryTypeIds;
    private MonthYear monthYear;
}
