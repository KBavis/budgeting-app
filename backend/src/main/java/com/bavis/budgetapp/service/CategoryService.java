package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.model.SubCategory;

public interface CategoryService {
	SubCategory create(SubCategory category) throws Exception;
	Category update(Category category);
	Category read(Long categoryId) throws Exception;
	void delete(Long categoryId);
}
