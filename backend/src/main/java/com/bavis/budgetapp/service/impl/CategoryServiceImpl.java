package com.bavis.budgetapp.service.impl;


import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service 
public class CategoryServiceImpl implements CategoryService{
	
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired 
	CategoryRepository categoryRepository;

	@Autowired
	CategoryTypeService categoryTypeService;

	@Autowired
	UserService userService;

	@Autowired
	CategoryMapper categoryMapper;




	@Override
	public List<Category> bulkCreate(BulkCategoryDto categoryDtos) {
		User user = userService.getCurrentAuthUser();
		CategoryType categoryType = categoryTypeService.read(categoryDtos.getCategories().get(0).getCategoryTypeId());

		//For Each Category DTO --> 1) set user, set category type, calculate budget amount
		List<Category> categories = categoryDtos.getCategories().stream()
				.map(categoryMapper::toEntity)
				.peek(category -> category.setCategoryType(categoryType))
				.peek(category -> category.setUser(user))
				.toList();

		return categoryRepository.saveAllAndFlush(categories);
	}

	@Override
	public Category create(Category category, Long categoryTypeId) throws Exception{
		LOG.info("Creating Category [{}] for category type with id [{}]", category, categoryTypeId);

		CategoryType type = categoryTypeService.read(categoryTypeId);

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
