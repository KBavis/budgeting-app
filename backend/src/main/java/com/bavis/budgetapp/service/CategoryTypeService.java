package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.entity.CategoryType;

import java.util.List;

public interface CategoryTypeService {
	CategoryType create(CategoryType categoryType);
	List<CategoryType> createMany(List<CategoryTypeDto> categoryTypeDtos);
	CategoryType update(CategoryType category, Long id);
	CategoryType read(Long categoryTypeId);
	List<CategoryType> readMany();
	void delete(Long categoryTypeId);
}
