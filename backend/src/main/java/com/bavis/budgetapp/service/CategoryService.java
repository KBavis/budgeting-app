package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.Category;

public interface CategoryService {
	Category create(Category category);
	Category update(Category category);
	Category read(Long categoryId);
	void delete(Long cateogyrId);
}
