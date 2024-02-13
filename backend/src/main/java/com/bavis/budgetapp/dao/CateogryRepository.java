package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CateogryRepository extends JpaRepository<Category, Long> {
	Category findByCategoryId(Long categoryId);
}
