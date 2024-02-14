package com.bavis.budgetapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.ParentCategoryRepository;
import com.bavis.budgetapp.dao.SubCategoryRepository;
import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.model.ParentCategory;
import com.bavis.budgetapp.model.SubCategory;
import com.bavis.budgetapp.service.CategoryService;

@Service 
public class CategoryServiceImpl implements CategoryService{
	
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired 
	SubCategoryRepository subCategoryRepository;
	
	@Autowired
	ParentCategoryRepository parentCategoryRepository;
	

	@Override
	public SubCategory create(SubCategory category) throws Exception {
		LOG.debug("Creating Category [{}]", category);
		return subCategoryRepository.save((SubCategory)category);
	}

	@Override
	public Category update(Category category){
		LOG.debug("Updating Category [{}]", category);
		
		if(category instanceof ParentCategory) {
			return parentCategoryRepository.save((ParentCategory) category);
		} else {
			return subCategoryRepository.save((SubCategory)category);
		} 
	}

	@Override
	public Category read(Long categoryId) throws Exception{
		LOG.debug("Reading Category with id [{}]", categoryId);
		
		Category category = subCategoryRepository.findByCategoryId(categoryId);
		
		if(category == null) {	//check if reading parent category
			category = parentCategoryRepository.findByCategoryId(categoryId);
			if(category == null) throw new RuntimeException("Invalid category id: " + categoryId);
		}
		
		return category;
	}

	@Override
	public void delete(Long categoryId) {
		LOG.debug("Deleting Category with id [{categoryId}]", categoryId);
		subCategoryRepository.deleteById(categoryId);
	}

}
