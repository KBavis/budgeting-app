package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyCategoryPerformanceRepository extends JpaRepository<MonthlyCategoryPerformance, Long> {

    /**
     * Retrieve MonthlyCategoryPerformances corresponding to CategoryTypeIds and specific MonthYear
     * @param categoryTypeIds
     *          - list of relevant CategoryTypeIds
     * @param monthYear
     *          - speciifc month year to retrieve records for
     * @return
     *          - persisted entities corresponding to inputs
     */
    List<MonthlyCategoryPerformance> findByCategoryTypeIdInAndMonthYear(List<Long> categoryTypeIds, MonthYear monthYear);
}
