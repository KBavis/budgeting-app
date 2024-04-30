package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.entity.Category;

import java.util.List;

public interface CategoryService {
	List<Category> bulkCreate(BulkCategoryDto categories);
	Category create(Category category, Long categoryTypeId) throws Exception;
	Category update(Category category, Long id);
	Category read(Long categoryId);
	void delete(Long categoryId);
}
