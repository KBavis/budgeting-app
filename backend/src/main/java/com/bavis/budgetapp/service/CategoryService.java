package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.Category;

public interface CategoryService {
	Category create(Category category) throws Exception;
	Category update(Category category);
	Category read(Long categoryId) throws Exception;
	void delete(Long categoryId);
}
