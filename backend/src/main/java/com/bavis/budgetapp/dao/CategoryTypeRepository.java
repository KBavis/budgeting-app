package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.entity.CategoryType;

public interface CategoryTypeRepository extends JpaRepository<CategoryType, Long> {
	CategoryType findByName(String categoryTypeName);
}
