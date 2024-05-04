package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 *  DAO for working with Income entities
 */
public interface IncomeRepository extends JpaRepository<Income, Long> {
    /**
     * Fetch relevant Income's based on a passed in User ID
     *
     * @param userId
     *          - user ID to search for incomes for
     * @return
     *          - list of Incomes pertaining to specific user
     */
    List<Income> findByUserUserId(Long userId);
}
