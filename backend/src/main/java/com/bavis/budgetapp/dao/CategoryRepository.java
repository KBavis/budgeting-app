package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category findByCategoryId(Long categoryId);
	Category findByName(String categoryName);
}

