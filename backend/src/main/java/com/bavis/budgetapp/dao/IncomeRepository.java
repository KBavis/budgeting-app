package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
}
