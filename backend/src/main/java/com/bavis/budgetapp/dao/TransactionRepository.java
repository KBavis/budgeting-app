package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Kellen Bavis
 *
 *  DAO for working with Transaction Entities
 */
public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
