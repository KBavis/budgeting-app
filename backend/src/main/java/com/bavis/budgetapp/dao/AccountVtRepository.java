package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bavis.budgetapp.entity.AccountVt;

/**
 * @author Kellen Bavis
 * 
 * DAO for working with AccountVt entities
 */
public interface AccountVtRepository extends JpaRepository<AccountVt, Long> {
}
