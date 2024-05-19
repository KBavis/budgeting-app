package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryTypeMapper;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryTypeService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	//TODO: Update this to use mapper
	//todo: finish this logic and add logging
	@Override
	public CategoryType update(CategoryType categoryType, Long id) {
		log.info("Updating CategoryType [{}]", id);
		CategoryType updatedCategory = repository.findById(id).orElse(categoryType);
		updatedCategory.setBudgetAllocationPercentage(categoryType.getBudgetAllocationPercentage());
		updatedCategory.setCategories(categoryType.getCategories());
		updatedCategory.setName(categoryType.getName());
		return repository.save(updatedCategory);
	}

	//todo: finish this logic and add logging
	@Override
	public CategoryType read(Long categoryTypeId) {
		log.info("Reading CategoryType with id [{}]", categoryTypeId);

        return repository.findById(categoryTypeId).orElseThrow(
				() -> (new RuntimeException("Invalid category type id: " + categoryTypeId)));
	}

	//todo: finish this logic and add logging
	@Override
	public void delete(Long categoryTypeId) {
		log.info("Deleting Category Type with id [{}]", categoryTypeId);
		repository.deleteById(categoryTypeId);
	}

}
