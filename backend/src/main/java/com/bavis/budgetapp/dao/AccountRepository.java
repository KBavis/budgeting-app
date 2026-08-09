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
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * Fetch Account entity by Account ID
     *
     * @param accountId
     *            - Account ID from Plaid API
     * @return
     *            - Account Entity corresponding to Plaid API Account ID
     */
    Optional<Account> findByAccountId(String accountId);

    /**
     * Fetch All Accounts Associated with Given User
     *
     * @param userId
     *             - user ID to fetch Accounts for
     * @return
     *              - all Account entities corresponding to given User
     */
    List<Account> findByUserUserId(Long userId);
}
