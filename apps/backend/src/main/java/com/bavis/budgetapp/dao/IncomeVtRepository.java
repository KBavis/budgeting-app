package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bavis.budgetapp.entity.IncomeVt;

/**
 * @author Kellen Bavis
 * 
 * DAO for working with IncomeVt entities
 */
public interface IncomeVtRepository extends JpaRepository<IncomeVt, Long> {
}
