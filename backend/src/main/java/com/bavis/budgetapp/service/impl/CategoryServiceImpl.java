package com.bavis.budgetapp.service.impl;


import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.dto.EditCategoryDto;
import com.bavis.budgetapp.dto.RenameCategoryDto;
import com.bavis.budgetapp.dto.UpdateCategoryDto;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	private CategoryRepository categoryRepository;

	@Autowired
	private CategoryTypeService categoryTypeService;

	@Autowired
	private UserService userService;

	@Autowired
	@Lazy
	private TransactionServiceImpl transactionService;

	@Autowired
	private CategoryMapper categoryMapper;




	@Override
	public List<Category> bulkCreate(BulkCategoryDto bulkCategoryDto) {
		log.info("Attempting to Bulk Create the following bulkCategoryDto: [{}]", bulkCategoryDto);

		User user = userService.getCurrentAuthUser();
		CategoryType categoryType = categoryTypeService.read(bulkCategoryDto.getCategories().get(0).getCategoryTypeId());
		log.info("CategoryType corresponding to BulkCategoryDto: [{}], corresponding User: [{}]", categoryType, user);

		//For Each Category DTO --> 1) set user, set category type, calculate budget amount
		List<Category> categories = bulkCategoryDto.getCategories().stream()
				.map(categoryMapper::toEntity)
				.peek(category -> category.setCategoryType(categoryType))
				.peek(category -> category.setUser(user))
				.toList();
		log.info("Successfully set User, CategoryType, and budget amount for each Category");

		//Update CategoryType Saved Amount
		double totalCategoryAllocations = categories.stream().mapToDouble(Category::getBudgetAmount).sum();
		double savedAmount = categoryType.getBudgetAmount() - totalCategoryAllocations;
		categoryType.setSavedAmount(savedAmount);
		log.info("CategoryType {} total saved amount based on newly added Categories: {}", categoryType.getCategoryTypeId(), savedAmount);

		return categoryRepository.saveAllAndFlush(categories);
	}

	@Override
	public List<Category> readAll() {
		log.info("Attempting to read all Categories corresponding to authenticated user");
		User currentAuthUser = userService.getCurrentAuthUser();
		return categoryRepository.findByUserUserId(currentAuthUser.getUserId());
	}

	@Override
	@Transactional
	public Category create(AddCategoryDto addCategoryDto) {
		log.info("Creating Category [{}] and updating following Categories: [{}]", addCategoryDto.getAddedCategory(), addCategoryDto.getUpdatedCategories());

		//Create New Category
		User authUser = userService.getCurrentAuthUser();
		CategoryDto categoryToAdd = addCategoryDto.getAddedCategory();
		CategoryType categoryType = categoryTypeService.read(categoryToAdd.getCategoryTypeId());
		Category createdCategory = categoryMapper.toEntity(categoryToAdd);
		createdCategory.setUser(authUser);
		createdCategory.setCategoryType(categoryType);

		//Update Existing Categories
        List<Category> updatedCategories = addCategoryDto.getUpdatedCategories().stream()
				.map(updateCategoryDto -> updateCategoryAllocation(updateCategoryDto, categoryType))
				.collect(Collectors.toList());
        List<Category> categoriesToSave = new ArrayList<>(updatedCategories);
		categoriesToSave.add(createdCategory);
		categoryRepository.saveAllAndFlush(categoriesToSave); //save new category and

		//Merge Existing Categories with Updated Categories
		List<Category> allCategories = mergeCategories(categoryType.getCategories(), updatedCategories, createdCategory);


		//Update CategoryType's Saving Amount
		double totalBudgetAmount = allCategories.stream()
				.mapToDouble(Category::getBudgetAmount)
				.sum();
		log.info("Total Budget Allocation for all Categories corresponding to CategoryType {} : {}", categoryType.getCategoryTypeId(), totalBudgetAmount);

		//Ensure BudgetAmount is Less Than CategoryType Allocation
		if(totalBudgetAmount > categoryType.getBudgetAmount()) {
			throw new RuntimeException("Category allocations, " + totalBudgetAmount + ", exceed total budgeted amount for CategoryType " + categoryType.getCategoryTypeId() + ": " + categoryType.getBudgetAmount());
		}

		//Update CategoryType Saved Amount
		categoryType.setSavedAmount(categoryType.getBudgetAmount() - totalBudgetAmount);

		return createdCategory;
	}

	@Override
	@Transactional
	public List<Category> updateCategoryAllocations(EditCategoryDto editCategoryDto){
		log.info("Updating Category allocations via the following EditCategoryDto: [{}]", editCategoryDto);

		//Validation Checks
		if(editCategoryDto == null) throw new RuntimeException("Invalid EditCategoryDto; ensures updates are not null");

		//Fetch Category Type
		CategoryType categoryType = categoryTypeService.read(editCategoryDto.getCategoryTypeId());

		//Update Category Budget Allocations
		List<Category> updatedCategories = editCategoryDto.getUpdatedCategories().stream()
				.map(updateCategoryDto -> updateCategoryAllocation(updateCategoryDto, categoryType))
				.collect(Collectors.toList());

		//Merge Existing Categories with Updates
		List<Category> mergedCategories = mergeCategories(categoryType.getCategories(), updatedCategories, null);

		//Update CategoryType's Saving Amount
		double totalBudgetAmount = mergedCategories.stream()
				.mapToDouble(Category::getBudgetAmount)
				.sum();
		log.info("Total Budget Allocation for all Categories corresponding to CategoryType {} : {}. Total CategoryType allocation available: {}", categoryType.getCategoryTypeId(), totalBudgetAmount, categoryType.getBudgetAmount());

		//Ensure BudgetAmount is Less Than CategoryType Allocation
		if(totalBudgetAmount > categoryType.getBudgetAmount()) {
			throw new RuntimeException("Category allocations, " + totalBudgetAmount + ", exceed total budgeted amount for CategoryType " + categoryType.getCategoryTypeId() + ": " + categoryType.getBudgetAmount());
		}

		//Update Category Type saved amount
		categoryType.setSavedAmount(categoryType.getBudgetAmount() - totalBudgetAmount);

		return updatedCategories;
	}

	@Override
	public Category renameCategory(RenameCategoryDto renameCategoryDto) {
		Category categoryToUpdate = categoryRepository.findByCategoryId(renameCategoryDto.getCategoryId());
		categoryToUpdate.setName(renameCategoryDto.getCategoryName());
		return categoryToUpdate;
	}

	@Override
	public Category read(Long categoryId){
		log.info("Reading Category with id [{}]", categoryId);
		
		Category category = categoryRepository.findByCategoryId(categoryId);
		
		if(category == null) {	//check if reading parent category
			 throw new RuntimeException("Invalid category id: " + categoryId);
		}
		
		return category;
	}

	@Override
	public void delete(Long categoryId) {
		log.info("Deleting Category with id [{}]", categoryId);

		//Fetch Category
		Category categoryToDelete = read(categoryId);

		//Fetch all Transactions corresponding to Category and update Category to be null
		Optional.ofNullable(transactionService.fetchCategoryTransactions(categoryId))
				.ifPresent(transactions -> transactions.forEach(transaction -> transactionService.removeAssignedCategory(transaction.getTransactionId())));

		//Update User and CategoryType to no longer correspond to Category
		userService.removeCategory(categoryToDelete);
		categoryTypeService.removeCategory(categoryToDelete);

		//Remove Category from DB
		categoryRepository.deleteById(categoryId);
	}

	/**
	 * Utility function to adjust existing Categories budgetAllocation
	 *
	 * @param updateCategoryDto
	 * 			- DTO containing Category ID and Budget Allocation Percentage updates
	 * @param categoryType
	 * 			- CategoryType this Category is associated with
	 * @return category
	 * 			- Updated Category
	 */
	private Category updateCategoryAllocation(UpdateCategoryDto updateCategoryDto, CategoryType categoryType) {
		double budgetAllocationPercentage = updateCategoryDto.getBudgetAllocationPercentage();
		Category category = read(updateCategoryDto.getCategoryId());
		category.setBudgetAllocationPercentage(budgetAllocationPercentage);
		category.setBudgetAmount(budgetAllocationPercentage * categoryType.getBudgetAmount());
		log.info("Updated Category {} with BudgetAllocationPercentage {} and BudgetAmount {} ", category.getCategoryId(), category.getBudgetAllocationPercentage(), category.getBudgetAmount());
		return category;
	}

	/**
	 * Utility function to merge existing Categories with updated Categories
	 *
	 * @param existingCategories
	 * 	-		- List of Category entities that previously existed
	 * @param updatedCategories
	 * 			- List of Category entities that were updated via Category creation
	 * @return
	 * 			- Merged List of Category entities
	 */
	private List<Category> mergeCategories(List<Category> existingCategories, List<Category> updatedCategories, Category newCategory) {
		Map<Long, Category> updatedCategoryMap = updatedCategories.stream()
				.collect(Collectors.toMap(Category::getCategoryId, category -> category));

		//Replace Existing Categories with Updated Categories and add new Category
		List<Category> mergedCategories = existingCategories.stream()
				.map(existingCategory -> updatedCategoryMap.getOrDefault(existingCategory.getCategoryId(), existingCategory))
				.collect(Collectors.toList());
	 	if(!mergedCategories.contains(newCategory) && newCategory != null)	{ mergedCategories.add(newCategory); }

		List<Long> categoryIds = mergedCategories.stream().map(Category::getCategoryId).toList();
		log.info("Merged Category Ids : [{}]", categoryIds);

		return mergedCategories;
	}

}
