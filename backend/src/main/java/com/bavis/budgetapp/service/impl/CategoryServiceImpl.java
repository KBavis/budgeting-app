package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.dto.request.AddCategoryDto;
import com.bavis.budgetapp.dto.request.BulkCategoryDto;
import com.bavis.budgetapp.dto.request.CategoryDto;
import com.bavis.budgetapp.dto.request.EditCategoryDto;
import com.bavis.budgetapp.dto.request.RenameCategoryDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.response.CategoryResponseDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryTypeVt;
import com.bavis.budgetapp.entity.CategoryVt;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryMapper;
import com.bavis.budgetapp.service.CategoryService;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.EffectivityService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Kellen Bavis
 *
 *         Implementation of our Category Service functionality
 */
@Service
@Log4j2
public class CategoryServiceImpl implements CategoryService {

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
	private EffectivityService effectivityService;

	@Autowired
	private CategoryMapper categoryMapper;

	/**
	 * Function to create multiple Category entities at once
	 *
	 * @param bulkCategoryDto
	 *                        - DTO to store information needed to create multiple
	 *                        Category entities
	 * @return
	 *         - List of created CategoryResponseDtos
	 */
	@Override
	public List<CategoryResponseDto> bulkCreate(BulkCategoryDto bulkCategoryDto) {
		log.info("Attempting to Bulk Create the following bulkCategoryDto: [{}]", bulkCategoryDto);

		User user = userService.getCurrentAuthUser();
		CategoryType categoryType = categoryTypeService
				.findEntity(bulkCategoryDto.getCategories().get(0).getCategoryTypeId(), null);
		log.info("CategoryType corresponding to BulkCategoryDto: [{}], corresponding User: [{}]", categoryType, user);

		List<Category> categories = bulkCategoryDto.getCategories().stream()
				.map(dto -> {
					Category category = new Category();
					category.setUser(user);
					CategoryVt initialVt = CategoryVt.builder()
							.category(category)
							.name(dto.getName())
							.budgetAllocationPercentage(dto.getBudgetAllocationPercentage())
							.budgetAmount(
									dto.getBudgetAllocationPercentage() * getCategoryTypeBudgetAmount(categoryType))
							.categoryType(categoryType)
							.build();
					effectivityService.applyVtUpdate(category.getValidTimes(), initialVt, LocalDate.now());
					return category;
				})
				.toList();
		log.info("Successfully set User, CategoryType, and budget amount for each Category");

		double totalCategoryAllocations = categories.stream()
				.mapToDouble(cat -> {
					CategoryVt active = effectivityService.getActiveVt(cat.getValidTimes(), LocalDate.now());
					return active.getBudgetAmount();
				})
				.sum();
		double savedAmount = getCategoryTypeBudgetAmount(categoryType) - totalCategoryAllocations;
		categoryTypeService.update(UpdateCategoryTypeDto.builder().savedAmount(savedAmount).build(),
				categoryType.getCategoryTypeId());
		log.info("CategoryType {} total saved amount based on newly added Categories: {}",
				categoryType.getCategoryTypeId(), savedAmount);

		List<Category> savedCategories = categoryRepository.saveAllAndFlush(categories);
		return savedCategories.stream()
				.map(cat -> {
					CategoryVt activeVt = effectivityService.getActiveVt(cat.getValidTimes(), LocalDate.now());
					return categoryMapper.toResponseDto(cat, activeVt);
				})
				.toList();
	}

	/**
	 * Function to fetch all Categories pertaining to authenticated user as of date
	 *
	 * @param asOf
	 *             - Point-in-time evaluation date
	 * @return
	 *         - all Categories corresponding to auth user
	 */
	@Override
	public List<Category> findAllEntities(LocalDate asOf) {
		log.info("Attempting to read all Categories corresponding to authenticated user asOf [{}]", asOf);
		User currentAuthUser = userService.getCurrentAuthUser();
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return categoryRepository.findByUserUserIdAndAsOf(currentAuthUser.getUserId(), target);
	}

	@Override
	public List<CategoryResponseDto> getAll(LocalDate asOf) {
		List<Category> categories = findAllEntities(asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return categories.stream()
				.map(cat -> {
					CategoryVt activeVt = effectivityService.getActiveVt(cat.getValidTimes(), target);
					return categoryMapper.toResponseDto(cat, activeVt);
				})
				.toList();
	}

	/**
	 * Function to create a single Category entity
	 *
	 * @param addCategoryDto
	 *                       - DTO used to create new Category and update existing
	 *                       Category allocations
	 * @return
	 *         - Created CategoryResponseDto
	 */
	@Override
	@Transactional
	public CategoryResponseDto create(AddCategoryDto addCategoryDto) {
		log.info("Creating Category [{}] and updating following Categories: [{}]", addCategoryDto.getAddedCategory(),
				addCategoryDto.getUpdatedCategories());

		User authUser = userService.getCurrentAuthUser();
		CategoryDto categoryToAdd = addCategoryDto.getAddedCategory();
		CategoryType categoryType = categoryTypeService.findEntity(categoryToAdd.getCategoryTypeId(), null);

		Category createdCategory = new Category();
		createdCategory.setUser(authUser);
		CategoryVt initialVt = CategoryVt.builder()
				.category(createdCategory)
				.name(categoryToAdd.getName())
				.budgetAllocationPercentage(categoryToAdd.getBudgetAllocationPercentage())
				.budgetAmount(categoryToAdd.getBudgetAllocationPercentage() * getCategoryTypeBudgetAmount(categoryType))
				.categoryType(categoryType)
				.build();
		effectivityService.applyVtUpdate(createdCategory.getValidTimes(), initialVt, LocalDate.now());

		List<Category> updatedCategories = addCategoryDto.getUpdatedCategories().stream()
				.map(updateCategoryDto -> updateCategoryAllocation(updateCategoryDto, categoryType))
				.collect(Collectors.toList());
		List<Category> categoriesToSave = new ArrayList<>(updatedCategories);
		categoriesToSave.add(createdCategory);
		categoryRepository.saveAllAndFlush(categoriesToSave);

		List<Category> allCategories = mergeCategories(categoryType.getCategories(), updatedCategories,
				createdCategory);

		double totalBudgetAmount = allCategories.stream()
				.mapToDouble(cat -> {
					CategoryVt active = effectivityService.getActiveVt(cat.getValidTimes(), LocalDate.now());
					return active.getBudgetAmount();
				})
				.sum();
		log.info("Total Budget Allocation for all Categories corresponding to CategoryType {} : {}",
				categoryType.getCategoryTypeId(), totalBudgetAmount);

		if (totalBudgetAmount > getCategoryTypeBudgetAmount(categoryType)) {
			throw new RuntimeException(
					"Category allocations, " + totalBudgetAmount + ", exceed total budgeted amount for CategoryType "
							+ categoryType.getCategoryTypeId() + ": " + getCategoryTypeBudgetAmount(categoryType));
		}

		double savedAmount = getCategoryTypeBudgetAmount(categoryType) - totalBudgetAmount;
		categoryTypeService.update(UpdateCategoryTypeDto.builder().savedAmount(savedAmount).build(),
				categoryType.getCategoryTypeId());

		CategoryVt activeVt = effectivityService.getActiveVt(createdCategory.getValidTimes(), LocalDate.now());
		return categoryMapper.toResponseDto(createdCategory, activeVt);
	}

	/**
	 * Function to update Category allocations
	 *
	 * @param editCategoryDto
	 *                        - DTO containing updated category allocations
	 * @return
	 *         - Updated CategoryResponseDtos
	 */
	@Override
	@Transactional
	public List<CategoryResponseDto> updateCategoryAllocations(EditCategoryDto editCategoryDto) {
		log.info("Updating Category allocations via the following EditCategoryDto: [{}]", editCategoryDto);

		if (editCategoryDto == null)
			throw new RuntimeException("Invalid EditCategoryDto; ensures updates are not null");

		CategoryType categoryType = categoryTypeService.findEntity(editCategoryDto.getCategoryTypeId(), null);

		List<Category> updatedCategories = editCategoryDto.getUpdatedCategories().stream()
				.map(updateCategoryDto -> updateCategoryAllocation(updateCategoryDto, categoryType))
				.collect(Collectors.toList());

		List<Category> mergedCategories = mergeCategories(categoryType.getCategories(), updatedCategories, null);

		double totalBudgetAmount = mergedCategories.stream()
				.mapToDouble(cat -> {
					CategoryVt active = effectivityService.getActiveVt(cat.getValidTimes(), LocalDate.now());
					return active.getBudgetAmount();
				})
				.sum();
		log.info(
				"Total Budget Allocation for all Categories corresponding to CategoryType {} : {}. Total CategoryType allocation available: {}",
				categoryType.getCategoryTypeId(), totalBudgetAmount, getCategoryTypeBudgetAmount(categoryType));

		if (totalBudgetAmount > getCategoryTypeBudgetAmount(categoryType)) {
			throw new RuntimeException(
					"Category allocations, " + totalBudgetAmount + ", exceed total budgeted amount for CategoryType "
							+ categoryType.getCategoryTypeId() + ": " + getCategoryTypeBudgetAmount(categoryType));
		}

		double updatedSavedAmount = getCategoryTypeBudgetAmount(categoryType) - totalBudgetAmount;
		categoryTypeService.update(UpdateCategoryTypeDto.builder().savedAmount(updatedSavedAmount).build(),
				categoryType.getCategoryTypeId());

		return updatedCategories.stream()
				.map(cat -> {
					CategoryVt activeVt = effectivityService.getActiveVt(cat.getValidTimes(), LocalDate.now());
					return categoryMapper.toResponseDto(cat, activeVt);
				})
				.toList();
	}

	/**
	 * Function to rename a Category
	 *
	 * @param renameCategoryDto
	 *                          - DTO containing CategoryId to update and updated
	 *                          name
	 * @return
	 *         - updated CategoryResponseDto
	 */
	@Override
	public CategoryResponseDto renameCategory(RenameCategoryDto renameCategoryDto) {
		log.info("Setting Category {} to have the name '{}'", renameCategoryDto.getCategoryId(),
				renameCategoryDto.getCategoryName());
		Category categoryToUpdate = findEntity(renameCategoryDto.getCategoryId(), null);

		CategoryVt active = effectivityService.getActiveVt(categoryToUpdate.getValidTimes(), LocalDate.now());
		CategoryVt updateVt = CategoryVt.builder()
				.category(categoryToUpdate)
				.name(renameCategoryDto.getCategoryName())
				.budgetAllocationPercentage(active.getBudgetAllocationPercentage())
				.budgetAmount(active.getBudgetAmount())
				.categoryType(active.getCategoryType())
				.build();

		effectivityService.applyVtUpdate(categoryToUpdate.getValidTimes(), updateVt, LocalDate.now());

		Category saved = categoryRepository.saveAndFlush(categoryToUpdate);
		CategoryVt activeVt = effectivityService.getActiveVt(saved.getValidTimes(), LocalDate.now());
		return categoryMapper.toResponseDto(saved, activeVt);
	}

	@Override
	public CategoryResponseDto get(Long categoryId, LocalDate asOf) {
		Category cat = findEntity(categoryId, asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		CategoryVt activeVt = effectivityService.getActiveVt(cat.getValidTimes(), target);
		return categoryMapper.toResponseDto(cat, activeVt);
	}

	/**
	 * Function to fetch a specific Category as of date
	 *
	 * @param categoryId
	 *                   - Category ID corresponding to specific Category to be
	 *                   fetched
	 * @param asOf
	 *                   - Point-in-time evaluation date
	 * @return
	 *         - fetched Category
	 */
	@Override
	public Category findEntity(Long categoryId, LocalDate asOf) {
		log.info("Reading Category with id [{}] asOf [{}]", categoryId, asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return categoryRepository.findByCategoryIdAndAsOf(categoryId, target)
				.orElseThrow(() -> new RuntimeException("Invalid category id: " + categoryId));
	}

	/**
	 * Function to delete a specific Category
	 *
	 * @param categoryId
	 *                   - Category ID corresponding to specific Category to be
	 *                   deleted
	 */
	@Override
	public void delete(Long categoryId) {
		log.info("Deleting Category with id [{}]", categoryId);

		Category categoryToDelete = findEntity(categoryId, null);
		categoryToDelete.setEndDate(LocalDate.now().minusDays(1));
		categoryRepository.save(categoryToDelete);
	}

	private Category updateCategoryAllocation(UpdateCategoryDto updateCategoryDto, CategoryType categoryType) {
		double budgetAllocationPercentage = updateCategoryDto.getBudgetAllocationPercentage();
		Category category = findEntity(updateCategoryDto.getCategoryId(), null);
		CategoryVt active = effectivityService.getActiveVt(category.getValidTimes(), LocalDate.now());

		double budgetAmount = budgetAllocationPercentage * getCategoryTypeBudgetAmount(categoryType);
		CategoryVt updateVt = CategoryVt.builder()
				.category(category)
				.name(active.getName())
				.budgetAllocationPercentage(budgetAllocationPercentage)
				.budgetAmount(budgetAmount)
				.categoryType(categoryType)
				.build();

		effectivityService.applyVtUpdate(category.getValidTimes(), updateVt, LocalDate.now());

		log.info("Updated Category {} with BudgetAllocationPercentage {} and BudgetAmount {} ",
				category.getCategoryId(), budgetAllocationPercentage, budgetAmount);
		return category;
	}

	private List<Category> mergeCategories(List<Category> existingCategories, List<Category> updatedCategories,
			Category newCategory) {
		Map<Long, Category> updatedCategoryMap = updatedCategories.stream()
				.collect(Collectors.toMap(Category::getCategoryId, category -> category));

		List<Category> mergedCategories = existingCategories.stream()
				.map(existingCategory -> updatedCategoryMap.getOrDefault(existingCategory.getCategoryId(),
						existingCategory))
				.collect(Collectors.toList());
		if (!mergedCategories.contains(newCategory) && newCategory != null) {
			mergedCategories.add(newCategory);
		}

		List<Long> categoryIds = mergedCategories.stream().map(Category::getCategoryId).toList();
		log.info("Merged Category Ids : [{}]", categoryIds);

		return mergedCategories;
	}

	private double getCategoryTypeBudgetAmount(CategoryType categoryType) {
		if (categoryType == null)
			return 0.0;
		CategoryTypeVt active = effectivityService.getActiveVt(categoryType.getValidTimes(), LocalDate.now());
		return active.getBudgetAmount();
	}

}
