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

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId IN :accountIds AND t.isDeleted = FALSE AND (t.date IS NOT NULL AND EXTRACT(MONTH FROM CAST(t.date AS date)) = EXTRACT(MONTH FROM CAST(:currentDate AS date)) AND EXTRACT(YEAR FROM CAST(t.date AS date)) = EXTRACT(YEAR FROM CAST(:currentDate AS date)))")
    List<Transaction> findByAccountIdsAndCurrentMonth(@Param("accountIds") List<String> accountIds, @Param("currentDate") LocalDate currentDate);

    /**
     * Determine whether a Transaction exists by a specified Transaction ID AND was updated by the user
     *
     * @param transactionId
     *          - Transaction ID to search for
     * @return
     *          - boolean determining if user updated Transaction and it exists within our DB
     */
    boolean existsByTransactionIdAndUpdatedByUserIsTrue(String transactionId);

    /**
     * Retrieve all Transactions for a given list of Category IDs, null Account, and within the current month/year
     *
     * @param categoryIds
     *          - Category IDs to fetch Transaction for
     * @param currentDate
     *          - current date to validate Transaction against
     * @return
     *          - all Transactions corresponding to current year/month, specified Category IDs, and no account associated with it
     */

    @Query("SELECT t FROM Transaction t WHERE t.category.categoryId IN :categoryIds " +
            "AND t.account IS NULL " +
            "AND (t.date IS NOT NULL AND EXTRACT(MONTH FROM CAST(t.date AS date)) = EXTRACT(MONTH FROM CAST(:currentDate AS date)) " +
            "AND EXTRACT(YEAR FROM CAST(t.date AS date)) = EXTRACT(YEAR FROM CAST(:currentDate AS date)))")
    List<Transaction> findByCategoryIdsAndCurrentMonth(@Param("categoryIds") List<Long> categoryIds, @Param("currentDate") LocalDate currentDate);


    /**
     * Retrieve all Transactions for a given Category ID
     *
     * @param categoryId
     *          - Category ID to fetch Transactions for
     * @return
     *          - List of Transaction entities corresponding to Category ID
     */
    List<Transaction> findByCategoryCategoryId(long categoryId);


    /**
     * Removal of all Transactions corresponding to a particular Account ID
     *
     * @param accountId
     *          - account ID to remove Transactions for
     */
    void deleteByAccountAccountId(String accountId);
}
