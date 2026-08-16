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

    @Query("""
            SELECT DISTINCT i FROM Income i 
            JOIN i.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
            WHERE i.incomeId = :incomeId 
              AND i.startDate <= :asOf AND i.endDate >= :asOf AND i.startDate <= i.endDate
            """)
    Optional<Income> findByIncomeIdAndAsOf(@Param("incomeId") Long incomeId, @Param("asOf") LocalDate asOf);

    @Query("""
            SELECT DISTINCT i FROM Income i 
            JOIN i.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
            WHERE i.user.userId = :userId 
              AND i.startDate <= :asOf AND i.endDate >= :asOf AND i.startDate <= i.endDate
            """)
    List<Income> findByUserUserIdAndAsOf(@Param("userId") Long userId, @Param("asOf") LocalDate asOf);
}
