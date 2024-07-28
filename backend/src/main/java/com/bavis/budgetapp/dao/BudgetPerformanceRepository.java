package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.BudgetPerformance;
import com.bavis.budgetapp.model.BudgetPerformanceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA Repository for working with BudgetPerformance entities
 *
 * @author Kellen Bavis
 */
public interface BudgetPerformanceRepository extends JpaRepository<BudgetPerformance, BudgetPerformanceId> {
    /**
     * Fetch all BudgetPerformances for a particular User
     *
     * @param userId
     *          - user ID to fetch BudgetPerformances for
     * @return
     *          - fetched BudgetPerformance entities
     */
     List<BudgetPerformance> findByUserUserId(Long userId);

    /**
     * Fetch BudgetPerformance for
     * @param month
     *          - month to fetch BudgetPerformance for
     * @param year
     *          - year to fetch BudgetPerformance for
     * @param userId
     *          - userId to fetch BudgetPerformance for
     * @return
     *          - BudgetPerformance entity
     */
     BudgetPerformance findByMonthYear_MonthAndMonthYear_YearAndUserUserId(String month, int year, Long userId);


}
