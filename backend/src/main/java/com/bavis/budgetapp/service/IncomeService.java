package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.entity.Income;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Service to house functionality regarding Income entities
 */
public interface IncomeService {
    /**
     * Functionality to create and persist an Income entity
     *
     * @param income
     *          - IncomeDto utilized to persist Income entity
     * @return
     *          - Persisted Income entity
     */
    Income create(IncomeDto income);

    /**
     * Functionality to fetch an Income entity by ID
     *
     * @param incomeId
     *          - ID corresponding to Income entity to be fetched
     * @return
     *          - Fetched Income entity corresponding to ID
     */
    Income readById(Long incomeId);

    /**
     * Functionality to retrieve Income entities based on associated User ID
     *
     * @param userId
     *          - ID of User whom is related to a particular Income entity
     * @return
     *          - List of Income entities corresponding to particular user
     */
    List<Income> readByUserId(Long userId);

    /**
     * Functionality to update a particular Income entity with updated attributes
     *
     * @param income
     *          - Income with updated propertied
     * @param incomeId
     *          - ID corresponding to Income entity to be updated
     * @return
     *          - Updated Income entity
     */
    Income update(Income income, Long incomeId);

    /**
     * Functionality to find the sum of all Income entities pertaining to a particular user
     *
     * @param userId
     *          - User ID to fetch Income entities for
     * @return
     *          - Total amount of each of the users Income's combined
     */
    double findUserTotalIncomeAmount(Long userId);

    /**
     * Functionality to delete a particular Income entity
     *
     * @param incomeId
     *          - ID corresponding to Income entity to be deleted
     */
    void detete(Long incomeId);

    /**
     * Functionality to read all Income entities associated with Authenticated User
     *
     * @return
     *      - all incomes associated with Auth user
     */
    List<Income> readAll();



}
