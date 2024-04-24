package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
}
