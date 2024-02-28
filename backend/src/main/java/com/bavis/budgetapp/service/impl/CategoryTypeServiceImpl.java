package com.bavis.budgetapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.model.CategoryType;
import com.bavis.budgetapp.service.CategoryTypeService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryTypeServiceImpl implements CategoryTypeService {
	private static Logger LOG = LoggerFactory.getLogger(CategoryTypeServiceImpl.class);
	private final CategoryTypeRepository repository;

	@Override
	public CategoryType create(CategoryType categoryType) {
		LOG.info("Creating CategoryType [{}]", categoryType);
		return repository.save(categoryType);
	}

	@Override
	public CategoryType update(CategoryType categoryType, Long id) {
		LOG.info("Updating CategoryType [{}]", id);
		CategoryType updatedCategory = repository.findById(id).orElse(categoryType);
		updatedCategory.setBudgetAllocation(categoryType.getBudgetAllocation());
		updatedCategory.setCategories(categoryType.getCategories());
		updatedCategory.setName(categoryType.getName());
		return repository.save(updatedCategory);
	}

	@Override
	public CategoryType read(Long categoryTypeId) {
		LOG.info("Reading CategoryType with id [{}]", categoryTypeId);

        return repository.findById(categoryTypeId).orElseThrow(
				() -> (new RuntimeException("Invalid category type id: " + categoryTypeId)));
	}

	@Override
	public List<CategoryType> readMany() {
		LOG.info("Reading all available CategoryTypes");

        return repository.findAll();
	}

	@Override
	public void delete(Long categoryTypeId) {
		LOG.debug("Deleting Category Type with id [{}]", categoryTypeId);
		repository.deleteById(categoryTypeId);
	}

}
