package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyCategoryPerformanceRepository extends JpaRepository<MonthlyCategoryPerformance, Long> {

    List<MonthlyCategoryPerformance> findByCategoryTypeIdAndMonthYear(long categoryTypeId, MonthYear monthYear);
}
