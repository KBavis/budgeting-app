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
    List<BudgetPerformance> findById_UserId(Long userId);

    /**
     * Fetch BudgetPerformance for a specific month, year, and user ID
     *
     * @param month
     *          - month to fetch BudgetPerformance for
     * @param year
     *          - year to fetch BudgetPerformance for
     * @param userId
     *          - userId to fetch BudgetPerformance for
     * @return
     *          - BudgetPerformance entity
     */
    BudgetPerformance findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(String month, int year, Long userId);
}

