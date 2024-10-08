package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryTypeMapper;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryTypeService;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Implementation of our CategoryType Service functionality
 */
@Service
@Log4j2
public class CategoryTypeServiceImpl implements CategoryTypeService {
	private CategoryTypeRepository repository;

	private UserService userService;

	private CategoryTypeMapper categoryTypeMapper;

	private IncomeService incomeService;

	public CategoryTypeServiceImpl(CategoryTypeRepository repository, UserService userService, CategoryTypeMapper categoryTypeMapper, IncomeService incomeService){
		this.repository = repository;
		this.userService = userService;
		this.categoryTypeMapper = categoryTypeMapper;
		this.incomeService = incomeService;
	}


	//TODO: Update this to use DTO
	//todo: finish this logic and add logging
	@Override
	public CategoryType create(CategoryType categoryType) {
		//Fetch Auth User to Associate To CategoryType
		User authUser = userService.getCurrentAuthUser();
		categoryType.setUser(authUser);

		log.info("Creating CategoryType [{}]", categoryType);
		return repository.save(categoryType);
	}


	@Override
	public void removeCategory(Category category) {
		if(category == null) return; //Ensure Not-Null

		log.info("Removing Category {} from corresponding CategoryType", category.getCategoryId());
		CategoryType categoryType = category.getCategoryType();
		if(categoryType != null) {
			List<Category> categoriesToUpdate = categoryType.getCategories() != null ?
				new ArrayList<>(categoryType.getCategories()) :
				new ArrayList<>();
			categoriesToUpdate.remove(category);
			categoryType.setCategories(categoriesToUpdate);

			//Calculate new savedAmount value
			double totalCategoryAllocations = categoriesToUpdate.stream().mapToDouble(Category::getBudgetAmount).sum();
			log.info("CategoryType {} updated saved amount after removal of Category {} : ${}", categoryType.getCategoryTypeId(), category.getCategoryId(), categoryType.getBudgetAmount() - totalCategoryAllocations);
			categoryType.setSavedAmount(categoryType.getBudgetAmount() - totalCategoryAllocations);

			List<Long> categoryIds = categoriesToUpdate.stream().map(Category::getCategoryId).toList();
			log.info("Updated list of Category Ids corresponding to CategoryType {}: [{}]", categoryType.getCategoryTypeId(), categoryIds);
			repository.save(categoryType);
		}
	}

	@Override
	public List<CategoryType> createMany(List<CategoryTypeDto> categoryTypeDtos) {
		log.info("Attempting to create many Categories: [{}]", categoryTypeDtos);

		User currentAuthUser = userService.getCurrentAuthUser();
		double userTotalIncome = incomeService.findUserTotalIncomeAmount(currentAuthUser.getUserId());
		log.debug("Total Income for user [{}] is [{}]", currentAuthUser, userTotalIncome);

		List<CategoryType> categoryTypes =
				categoryTypeDtos.stream().map((categoryTypeDto -> categoryTypeMapper.toEntity(categoryTypeDto)))
						.peek(categoryType -> categoryType.setUser(currentAuthUser))
						.peek(categoryType -> categoryType.setCategories(new ArrayList<>()))
						.peek(categoryType -> categoryType.setBudgetAmount(userTotalIncome * categoryType.getBudgetAllocationPercentage()))
						.toList();
		log.debug("Successfully mapped CategoryTypes to corresponding user and correctly allocated amounts of income");
		return repository.saveAllAndFlush(categoryTypes);
	}

	@Override
	public List<CategoryType> readAll() {
		log.info("Attempting to read all CategoryTypes for the current authenticated user");
		User currentAuthUser = userService.getCurrentAuthUser();
		return repository.findByUserUserId(currentAuthUser.getUserId());
	}

	@Override
	public List<CategoryType> readAll(User user) {
		log.info("Attempting to read all CategoryTypes for the User {}", user.getUserId());
		return repository.findByUserUserId(user.getUserId());
	}

	@Override
	public CategoryType update(UpdateCategoryTypeDto updateCategoryTypeDto, Long id) {
		//Fetch CategoryType or throw NotFoundException
		CategoryType categoryType = read(id);
		log.info("CategoryType [{}] updates via the following UpdateCategoryTypeDto [{}]", categoryType, updateCategoryTypeDto);

		//Update CategoryType
		categoryType.setSavedAmount(updateCategoryTypeDto.getSavedAmount());
		categoryType.setBudgetAllocationPercentage(updateCategoryTypeDto.getBudgetAllocationPercentage());
		categoryType.setBudgetAmount(updateCategoryTypeDto.getAmountAllocated());

		//Persist Updates
		return repository.save(categoryType);
	}

	@Override
	public CategoryType read(Long categoryTypeId) {
		log.info("Reading CategoryType with id [{}]", categoryTypeId);

        return repository.findById(categoryTypeId).orElseThrow(
				() -> (new RuntimeException("Invalid category type id: " + categoryTypeId)));
	}

	@Override
	public CategoryType readByName(String categoryTypeName) {
		User currentAuthUser = userService.getCurrentAuthUser();
		long userId = currentAuthUser.getUserId();
		String normalCaseType = GeneralUtil.toNormalCase(categoryTypeName);
		log.info("Attempting to fetch Category Type with the name {} for User {}", normalCaseType, userId);
		return repository.findByNameAndUserUserId(normalCaseType, userId);
	}

	@Override
	public CategoryType readByName(String categoryTypeName, User user) {
		long userId = user.getUserId();
		String normalCaseType = GeneralUtil.toNormalCase(categoryTypeName);
		log.info("Attempting to fetch Category Type with the name {} for User {}", normalCaseType, userId);
		return repository.findByNameAndUserUserId(normalCaseType, userId);
	}



	//todo: finish this logic and add logging
	@Override
	public void delete(Long categoryTypeId) {
		log.info("Deleting Category Type with id [{}]", categoryTypeId);
		repository.deleteById(categoryTypeId);
	}

}
