package com.bavis.budgetapp.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryService;

@Service 
public class CategoryServiceImpl implements CategoryService{
	
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired 
	CategoryRepository categoryRepository;
	
	@Autowired
	CategoryTypeRepository categoryTypeRepository;
	


	@Override
	public Category create(Category category, Long categoryTypeId) throws Exception{
		LOG.info("Creating Category [{}] for category type with id [{}]", category, categoryTypeId);
		
		CategoryType type = categoryTypeRepository.findById(categoryTypeId).orElseThrow(() -> new Exception("Category Type not found for id " + categoryTypeId));
		
		category.setCategoryType(type);
		//TODO: Set User of this category to be the authenticated user once JWT established
		return categoryRepository.save(category);
	}

	@Override
	public Category update(Category category, Long id){
		LOG.info("Updating Category [{}]", id);
		
		Category cat = categoryRepository.findById(id).orElse(category);
		cat.setCategoryType(category.getCategoryType());
		cat.setName(category.getName());
		cat.setUser(category.getUser());
		return categoryRepository.save(cat);
	}

	@Override
	public Category read(Long categoryId){
		LOG.info("Reading Category with id [{}]", categoryId);
		
		Category category = categoryRepository.findByCategoryId(categoryId);
		
		if(category == null) {	//check if reading parent category
			 throw new RuntimeException("Invalid category id: " + categoryId);
		}
		
		return category;
	}

	@Override
	public void delete(Long categoryId) {
		LOG.info("Deleting Category with id [{categoryId}]", categoryId);
		categoryRepository.deleteById(categoryId);
	}

}
