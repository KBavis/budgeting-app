package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.StagedVenmoPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * DAO for working with StagedVenmoPayment entities
 */
public interface StagedVenmoPaymentRepository extends JpaRepository<StagedVenmoPayment, Long> {

    /**
     * Find all unmatched staged Venmo payments for a user.
     * Used during Plaid transaction sync to enrich newly imported transactions.
     *
     * @param userId
     *          - the user's ID
     * @return
     *          - list of unmatched staged payments ordered by email timestamp (newest first)
     */
    List<StagedVenmoPayment> findByUserUserIdAndMatchedIsFalseOrderByEmailTimestampDesc(Long userId);

    /**
     * Find unmatched staged Venmo payments for a user from a given start timestamp onward.
     * Ordered chronologically (oldest first) to preserve sequence of intra-day payments.
     *
     * @param userId
     *          - the user's ID
     * @param fromTimestamp
     *          - cutoff timestamp (e.g. start of previous month)
     * @return
     *          - list of unmatched staged payments ordered by email timestamp ASC
     */
    List<StagedVenmoPayment> findByUserUserIdAndMatchedIsFalseAndEmailTimestampGreaterThanEqualOrderByEmailTimestampAsc(Long userId, LocalDateTime fromTimestamp);

    /**
     * Find all staged payments for a user (matched or unmatched) for UI display.
     *
     * @param userId
     *          - the user's ID
     * @return
     *          - list of all staged payments
     */
    List<StagedVenmoPayment> findByUserUserIdOrderByEmailTimestampDesc(Long userId);
}
