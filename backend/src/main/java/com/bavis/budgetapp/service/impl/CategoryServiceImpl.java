package com.bavis.budgetapp.service.impl;


import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryService;

import java.util.List;

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

	@Override
	public Category create(AddCategoryDto addCategoryDto) {
		log.info("Creating Category [{}] and updating following Categories: [{}]", addCategoryDto.getAddedCategory(), addCategoryDto.getUpdatedCategories());

		//Create New Category
		User authUser = userService.getCurrentAuthUser();
		CategoryType categoryType = categoryTypeService.read(addCategoryDto.getAddedCategory().getCategoryTypeId());
		Category createdCategory = categoryMapper.toEntity(addCategoryDto.getAddedCategory());
		createdCategory.setUser(authUser);
		createdCategory.setCategoryType(categoryType);

		/*
			Persist Updates to Existing Categories

			1. Fetch Category based on UpdateCategoryId
			2. Set their Budget Allocation Percentage
			3. Set their Budget Allocation Amount [BudgetAllocationPercentage * CategoryType.amount]
			4. Persist
		 */

		/*
			Update Category Type's Saving Amount

			1. Determine Category Type's Allocated Amount
			2. Determine Total Sum of Budget Allocation Amount for each Category pertaining to CategoryType [NOTE: The Total Sum of Categories BudgetAmount MUST BE Less Than CategoryType Amount]
			3. Update CategoryType Saving Amount [CategoryType.amount - Total Sum of Budget Allocation Amount]
			4. Persist CategoryType
		 */

		return null; //TODO: delete me
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
