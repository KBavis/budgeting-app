package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.CategoryType;

import java.util.List;

public interface CategoryTypeService {
	CategoryType create(CategoryType categoryType);
	CategoryType update(CategoryType category, Long id);	
	CategoryType read(Long categoryTypeId);
	List<CategoryType> readMany();
	void delete(Long categoryTypeId);
}
