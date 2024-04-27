package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserUserId(Long userId);
}
