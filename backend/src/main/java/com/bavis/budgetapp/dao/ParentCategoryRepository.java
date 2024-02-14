package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.model.ParentCategory;

public interface ParentCategoryRepository extends JpaRepository<ParentCategory, Long> {
	ParentCategory findByCategoryId(Long categoryId);
	ParentCategory findByName(String categoryName);
}

