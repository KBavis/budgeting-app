package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.request.IncomeDto;
import com.bavis.budgetapp.dto.request.UpdateIncomeDto;
import com.bavis.budgetapp.dto.response.IncomeResponseDto;
import com.bavis.budgetapp.entity.Income;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Service to house functionality regarding Income entities
 */
public interface IncomeService {
    /**
     * Functionality to create and persist an Income entity, returning an IncomeResponseDto
     *
     * @param incomeDto
     *          - IncomeDto utilized to persist Income entity
     * @return
     *          - IncomeResponseDto corresponding to persisted Income
     */
    IncomeResponseDto create(IncomeDto incomeDto);

    /**
     * Functionality to read all Income entities associated with Authenticated User as of date
     *
     * @param asOf
     *          - Point-in-time date
     * @return
     *      - all incomes associated with Auth user as of date
     */
    List<Income> findAllEntities(LocalDate asOf);

    /**
     * Functionality to read all IncomeResponseDtos associated with Authenticated User as of date
     *
     * @param asOf
     *          - Point-in-time date
     * @return
     *      - all IncomeResponseDtos associated with Auth user as of date
     */
    List<IncomeResponseDto> getAll(LocalDate asOf);

    /**
     * Functionality to fetch an Income entity by ID as of date
     *
     * @param incomeId
     *          - ID corresponding to Income entity to be fetched
     * @param asOf
     *          - Point-in-time date
     * @return
     *          - Fetched Income entity corresponding to ID
     */
    Income findEntity(Long incomeId, LocalDate asOf);

    /**
     * Functionality to retrieve Income entities based on associated User ID as of date
     *
     * @param userId
     *          - ID of User whom is related to a particular Income entity
     * @param asOf
     *          - Point-in-time date
     * @return
     *          - List of Income entities corresponding to particular user
     */
    List<Income> findAllEntitiesByUserId(Long userId, LocalDate asOf);

    /**
     * Functionality to find the sum of all Income entities pertaining to a particular user as of date
     *
     * @param userId
     *          - User ID to fetch Income entities for
     * @param asOf
     *          - Point-in-time date
     * @return
     *          - Total amount of each of the users Income's combined as of date
     */
    double findUserTotalIncomeAmount(Long userId, LocalDate asOf);

    /**
     * Functionality to update a particular Income entity with updated attributes
     *
     * @param incomeDto
     *          - updated income amount & corresponding Income ID to update
     * @return
     *          - Updated IncomeResponseDto
     */
    IncomeResponseDto update(UpdateIncomeDto incomeDto);

    /**
     * Functionality to delete a particular Income entity
     *
     * @param incomeId
     *          - ID corresponding to Income entity to be deleted
     */
    void delete(Long incomeId);
}
