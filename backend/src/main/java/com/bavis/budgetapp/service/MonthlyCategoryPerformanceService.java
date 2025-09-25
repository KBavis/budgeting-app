package com.bavis.budgetapp.service;

import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;

import java.util.List;

/**
 * Service for interacting with MonthlyCategoryPerformance objects for user
 *
 */
public interface MonthlyCategoryPerformanceService {

    /***
     * Generate granular break down of users spending broken down by merchant and category
     *
     * @param userId
     *          - user ID
     * @param monthYear
     *          - specific month / year this applies to
     * @param categories
     *          - list of user Categories
     */
    void generateMonthlyCategoryPerformances(Long userId, MonthYear monthYear, List<Category> categories);

    /**
     * Retrieve relevant MonthlyCategoryPerformances for specific CategoryType & MonthYear
     *
     * @param categoryTypeId
     *          - category type to retrieve MonthlyCategoryPerformance for
     * @param monthYear
     *          - relevant Month Year to retireve MonthlyCategoryPerformance for
     * @return
     *          - list of relevant MonthlyCategoryPerformances corresponding to inputs
     */
    List<MonthlyCategoryPerformance> getPerformances(Long categoryTypeId, MonthYear monthYear);
}
