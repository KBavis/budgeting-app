package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
