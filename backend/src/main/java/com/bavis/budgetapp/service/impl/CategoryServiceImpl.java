package com.bavis.budgetapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CateogryRepository;
import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.service.CategoryService;

@Service 
public class CategoryServiceImpl implements CategoryService{
	
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired 
	CateogryRepository categoryRepository;

	@Override
	public Category create(Category category) {
		LOG.debug("Creating Category [{}]", category);
		return categoryRepository.save(category);
	}

	@Override
	public Category update(Category category) {
		LOG.debug("Updating Category [{}]", category);
		return categoryRepository.save(category);
	}

	@Override
	public Category read(Long categoryId) {
		LOG.debug("Reading Category with id [{}]", categoryId);
		
		Category category = categoryRepository.findByCategoryId(categoryId);
		
		if(category == null) {
			throw new RuntimeException("Invalid category id: " + categoryId);
		}
		
		return category;
	}

	@Override
	public void delete(Long categoryId) {
		LOG.debug("Deleting Category with id [{categoryId}]", categoryId);
		categoryRepository.deleteById(categoryId);
	}

}
