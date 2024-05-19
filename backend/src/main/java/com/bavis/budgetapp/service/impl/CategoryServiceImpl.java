package com.bavis.budgetapp.service.impl;


import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
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

/**
 * @author Kellen Bavis
 *
 * Implementation of our Category Service functionality
 */
@Service
@Log4j2
public class CategoryServiceImpl implements CategoryService{

	@Autowired 
	CategoryRepository categoryRepository;

	@Autowired
	CategoryTypeService categoryTypeService;

	@Autowired
	UserService userService;

	@Autowired
	CategoryMapper categoryMapper;




	@Override
	public List<Category> bulkCreate(BulkCategoryDto bulkCategoryDto) {
		log.info("Attempting to Bulk Create the following bulkCategoryDto: [{}]", bulkCategoryDto);

		User user = userService.getCurrentAuthUser();
		CategoryType categoryType = categoryTypeService.read(bulkCategoryDto.getCategories().get(0).getCategoryTypeId());
		log.debug("CategoryType corresponding to BulkCategoryDto: [{}], corresponding User: [{}]", categoryType, user);

		//For Each Category DTO --> 1) set user, set category type, calculate budget amount
		List<Category> categories = bulkCategoryDto.getCategories().stream()
				.map(categoryMapper::toEntity)
				.peek(category -> category.setCategoryType(categoryType))
				.peek(category -> category.setUser(user))
				.toList();
		log.debug("Successfully set User, CategoryType, and budget amount for each Category");

		return categoryRepository.saveAllAndFlush(categories);
	}

	@Override
	public List<Category> readAll() {
		log.info("Attempting to read all Categories corresponding to authenticated user");
		User currentAuthUser = userService.getCurrentAuthUser();
		return categoryRepository.findByUserUserId(currentAuthUser.getUserId());
	}

	//todo: finish this logic and add logging
	@Override
	public Category create(Category category, Long categoryTypeId) throws Exception{
		log.info("Creating Category [{}] for category type with id [{}]", category, categoryTypeId);

		CategoryType type = categoryTypeService.read(categoryTypeId);

		category.setCategoryType(type);
		//TODO: Set User of this category to be the authenticated user once JWT established
		return categoryRepository.save(category);
	}

	//todo: finish this logic and add logging
	@Override
	public Category update(Category category, Long id){
		log.info("Updating Category with ID {} with the following Category entity: [{}]", id, category);
		
		Category cat = categoryRepository.findById(id).orElse(category);
		cat.setCategoryType(category.getCategoryType());
		cat.setName(category.getName());
		cat.setUser(category.getUser());
		return categoryRepository.save(cat);
	}

	//todo: finish this logic and add logging
	@Override
	public Category read(Long categoryId){
		log.info("Reading Category with id [{}]", categoryId);
		
		Category category = categoryRepository.findByCategoryId(categoryId);
		
		if(category == null) {	//check if reading parent category
			 throw new RuntimeException("Invalid category id: " + categoryId);
		}
		
		return category;
	}

	//todo: finish this logic and add logging
	@Override
	public void delete(Long categoryId) {
		log.info("Deleting Category with id [{}]", categoryId);
		categoryRepository.deleteById(categoryId);
	}

}
