package com.bavis.budgetapp.service;

import com.bavis.budgetapp.entity.analysis.BudgetPerformance;
import com.bavis.budgetapp.model.MonthYear;

import java.util.List;

/**
 * Service for interacting with BudgetPerformance entities
 *
 * @author Kellen Bavis
 */
public interface  BudgetPerformanceService {

    /**
     * Functionality to retrieve all relevant BudgetPerformances for the currently authenticated user
     *
     * @return
     *      - all relevant BudgetPerformance entities corresponding to auth User
     */
    List<BudgetPerformance> fetchBudgetPerformances();

    /**
     * Functionality to kick off our Budget Performance job
     *
     * @param monthYear
     *     - unique month/year to run Job for
     */
    void runGenerateBudgetPerformanceJob(MonthYear monthYear);

    /**
     * Functionality to retrieve a BudgetPerformance for a particular Month/Year
     *
     * @param monthYear
     *          - Month/Year to fetch BudgetPerformance for
     * @return
     *          - BudgetPerformance entity
     */
    BudgetPerformance fetchBudgetPerformance(MonthYear monthYear);


}
