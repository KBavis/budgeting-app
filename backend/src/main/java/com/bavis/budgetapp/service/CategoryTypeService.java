package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.CategoryType;

public interface CategoryTypeService {
	CategoryType create(CategoryType categoryType);
	CategoryType update(CategoryType category, Long id);	
	CategoryType read(Long categoryTypeId);
	void delete(Long categoryTypeId);
}
