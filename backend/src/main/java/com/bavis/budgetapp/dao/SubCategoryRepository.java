package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.model.ParentCategory;
import com.bavis.budgetapp.model.SubCategory;


public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
	SubCategory findByCategoryId(Long categoryId);
	SubCategory findByName(String categoryName);
}

