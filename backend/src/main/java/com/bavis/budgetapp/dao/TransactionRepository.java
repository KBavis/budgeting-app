package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 *  DAO for working with Transaction Entities
 */
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Retrieve all Transactions for a given list of Account IDs and Within the Current Month/Year
     *
     * @param accountIds
     *          - account IDs to fetch Transactions for
     * @param currentDate
     *          - current date to validate Transactions against
     * @return
     *          - all Transactions corresponding to current year/month and specified Account IDss
     */

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId IN :accountIds AND (t.date IS NOT NULL AND EXTRACT(MONTH FROM CAST(t.date AS date)) = EXTRACT(MONTH FROM CAST(:currentDate AS date)) AND EXTRACT(YEAR FROM CAST(t.date AS date)) = EXTRACT(YEAR FROM CAST(:currentDate AS date)))")
    List<Transaction> findByAccountIdsAndCurrentMonth(@Param("accountIds") List<String> accountIds, @Param("currentDate") LocalDate currentDate);
}
