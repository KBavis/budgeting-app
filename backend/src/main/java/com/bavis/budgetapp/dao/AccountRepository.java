package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.entity.Account;

/**
 * @author Kellen Bavis
 *
 * DAO for working with Account entities
 */
public interface AccountRepository extends JpaRepository<Account, Long> { }
