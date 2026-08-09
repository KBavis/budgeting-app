package com.bavis.budgetapp.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bavis.budgetapp.entity.Account;

/**
 * @author Kellen Bavis
 *
 * DAO for working with Account entities
 */
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("""
            SELECT DISTINCT a FROM Account a 
            JOIN a.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
            WHERE a.accountId = :accountId 
              AND a.startDate <= :asOf AND a.endDate >= :asOf AND a.startDate <= a.endDate
            """)
    Optional<Account> findByAccountIdAndAsOf(@Param("accountId") String accountId, @Param("asOf") LocalDate asOf);

    @Query("""
            SELECT DISTINCT a FROM Account a 
            JOIN a.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
            WHERE a.user.userId = :userId 
              AND a.startDate <= :asOf AND a.endDate >= :asOf AND a.startDate <= a.endDate
            """)
    List<Account> findByUserUserIdAndAsOf(@Param("userId") Long userId, @Param("asOf") LocalDate asOf);
}
