package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyCategoryPerformanceRepository extends JpaRepository<MonthlyCategoryPerformance, Long> {
}
