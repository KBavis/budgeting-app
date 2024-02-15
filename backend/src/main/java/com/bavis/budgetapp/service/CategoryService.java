package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.Category;

public interface CategoryService {
	Category create(Category category, Long categoryTypeId) throws Exception;
	Category update(Category category, Long id);
	Category read(Long categoryId);
	void delete(Long categoryId);
}
