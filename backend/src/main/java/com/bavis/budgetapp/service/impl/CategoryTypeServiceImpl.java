package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.dto.request.CategoryTypeDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.response.CategoryTypeResponseDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryTypeVt;
import com.bavis.budgetapp.entity.CategoryVt;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.mapper.CategoryTypeMapper;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.EffectivityService;
import com.bavis.budgetapp.service.IncomeService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
	private final CategoryTypeRepository repository;
	private final UserService userService;
	private final IncomeService incomeService;
	private final EffectivityService effectivityService;
	private final CategoryTypeMapper categoryTypeMapper;

	public CategoryTypeServiceImpl(CategoryTypeRepository repository, UserService userService,
			IncomeService incomeService, EffectivityService effectivityService, CategoryTypeMapper categoryTypeMapper) {
		this.repository = repository;
		this.userService = userService;
		this.incomeService = incomeService;
		this.effectivityService = effectivityService;
		this.categoryTypeMapper = categoryTypeMapper;
	}

	/**
	 * Function to create a Category Type entity from a CategoryTypeDto
	 *
	 * @param categoryTypeDto
	 *                        - Category Type DTO containing creation attributes
	 * @return
	 *         - Saved CategoryTypeResponseDto
	 */
	@Override
	public CategoryTypeResponseDto create(CategoryTypeDto categoryTypeDto) {
		log.info("Creating CategoryType from DTO [{}]", categoryTypeDto);
		User currentAuthUser = userService.getCurrentAuthUser();
		double userTotalIncome = incomeService.findUserTotalIncomeAmount(currentAuthUser.getUserId(), null);

		CategoryType ct = new CategoryType();
		ct.setUser(currentAuthUser);
		ct.setCategories(new ArrayList<>());

		CategoryTypeVt initialVt = CategoryTypeVt.builder()
				.categoryType(ct)
				.name(categoryTypeDto.getName())
				.budgetAllocationPercentage(categoryTypeDto.getBudgetAllocationPercentage())
				.budgetAmount(userTotalIncome * categoryTypeDto.getBudgetAllocationPercentage())
				.savedAmount(0.0)
				.build();
		effectivityService.applyVtUpdate(ct.getValidTimes(), initialVt, LocalDate.now());

		CategoryType saved = repository.save(ct);
		CategoryTypeVt activeVt = effectivityService.getActiveVt(saved.getValidTimes(), LocalDate.now());
		return categoryTypeMapper.toResponseDto(saved, activeVt);
	}

	/**
	 * Function to remove a particular Category from a CategoryType entity
	 *
	 * @param category
	 *                 - Category to be removed
	 */
	@Override
	public void removeCategory(Category category) {
		if (category == null)
			return;

		LocalDate today = LocalDate.now();
		log.info("Removing Category {} from corresponding CategoryType", category.getCategoryId());
		CategoryVt catVt = effectivityService.getActiveVt(category.getValidTimes(), today);
		CategoryType categoryType = catVt.getCategoryType();
		if (categoryType != null) {
			List<Category> categoriesToUpdate = categoryType.getCategories() != null
					? new ArrayList<>(categoryType.getCategories())
					: new ArrayList<>();
			categoriesToUpdate.remove(category);
			categoryType.setCategories(categoriesToUpdate);

			double totalCategoryAllocations = categoriesToUpdate.stream()
					.mapToDouble(cat -> {
						CategoryVt cv = effectivityService.getActiveVt(cat.getValidTimes(), today);
						return cv.getBudgetAmount();
					})
					.sum();
			CategoryTypeVt active = effectivityService.getActiveVt(categoryType.getValidTimes(), today);
			double currentBudgetAmount = active.getBudgetAmount();
			double newSavedAmount = currentBudgetAmount - totalCategoryAllocations;
			log.info("CategoryType {} updated saved amount after removal of Category {} : ${}",
					categoryType.getCategoryTypeId(), category.getCategoryId(), newSavedAmount);

			CategoryTypeVt updateVt = CategoryTypeVt.builder()
					.categoryType(categoryType)
					.name(active.getName())
					.budgetAllocationPercentage(active.getBudgetAllocationPercentage())
					.budgetAmount(currentBudgetAmount)
					.savedAmount(newSavedAmount)
					.build();
			effectivityService.applyVtUpdate(categoryType.getValidTimes(), updateVt, today);

			List<Long> categoryIds = categoriesToUpdate.stream().map(Category::getCategoryId).toList();
			log.info("Updated list of Category Ids corresponding to CategoryType {}: [{}]", categoryType.getCategoryTypeId(), categoryIds);
			repository.save(categoryType);
		}
	}

	/**
	 * Function to create multiple Category Type entities in a singular request
	 *
	 * @param categoryTypeDtos
	 *                         - List of Category Type DTOs utilized to persist
	 *                         CategoryType entities
	 * @return
	 *         - List of persisted CategoryTypeResponseDtos
	 */
	@Override
	public List<CategoryTypeResponseDto> createMany(List<CategoryTypeDto> categoryTypeDtos) {
		log.info("Attempting to create many Categories: [{}]", categoryTypeDtos);

		User currentAuthUser = userService.getCurrentAuthUser();
		double userTotalIncome = incomeService.findUserTotalIncomeAmount(currentAuthUser.getUserId(), null);
		log.debug("Total Income for user [{}] is [{}]", currentAuthUser, userTotalIncome);

		List<CategoryType> categoryTypes = categoryTypeDtos.stream()
				.map(dto -> {
					CategoryType ct = new CategoryType();
					ct.setUser(currentAuthUser);
					ct.setCategories(new ArrayList<>());
					CategoryTypeVt initialVt = CategoryTypeVt.builder()
							.categoryType(ct)
							.name(dto.getName())
							.budgetAllocationPercentage(dto.getBudgetAllocationPercentage())
							.budgetAmount(userTotalIncome * dto.getBudgetAllocationPercentage())
							.savedAmount(0.0)
							.build();
					effectivityService.applyVtUpdate(ct.getValidTimes(), initialVt, LocalDate.now());
					return ct;
				})
				.toList();
		log.debug("Successfully mapped CategoryTypes to corresponding user and correctly allocated amounts of income");
		List<CategoryType> savedList = repository.saveAllAndFlush(categoryTypes);
		return savedList.stream()
				.map(ct -> {
					CategoryTypeVt activeVt = effectivityService.getActiveVt(ct.getValidTimes(), LocalDate.now());
					return categoryTypeMapper.toResponseDto(ct, activeVt);
				})
				.toList();
	}

	/**
	 * Functionality to read all CategoryTypes pertaining to authenticated user as
	 * of date
	 *
	 * @param asOf
	 *             - Point-in-time date
	 * @return
	 *         - all CategoryTypes corresponding to authenticated user as of date
	 */
	@Override
	public List<CategoryType> findAllEntities(LocalDate asOf) {
		User currentAuthUser = userService.getCurrentAuthUser();
		return findAllEntities(currentAuthUser, asOf);
	}

	@Override
	public List<CategoryTypeResponseDto> getAll(LocalDate asOf) {
		List<CategoryType> categoryTypes = findAllEntities(asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return categoryTypes.stream()
				.map(ct -> {
					CategoryTypeVt activeVt = effectivityService.getActiveVt(ct.getValidTimes(), target);
					return categoryTypeMapper.toResponseDto(ct, activeVt);
				})
				.toList();
	}

	/**
	 * Functionality to read all CategoryTypes pertaining to a specific User as of
	 * date
	 *
	 * @param user
	 *             - User to fetch all CategoryTypes for
	 * @param asOf
	 *             - Point-in-time date
	 * @return
	 *         - all CategoryTypes corresponding to user as of date
	 */
	@Override
	public List<CategoryType> findAllEntities(User user, LocalDate asOf) {
		log.info("Attempting to read all CategoryTypes for User {} asOf [{}]", user.getUserId(), asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return repository.findByUserUserIdAndAsOf(user.getUserId(), target);
	}

	/**
	 * Function to update a CategoryType with updated properties
	 *
	 * @param updateCategoryTypeDto
	 *                              - DTO containing relevant CategoryType updates
	 * @param id
	 *                              - ID corresponding to CategoryType to be updated
	 * @return
	 *         - Updated and saved CategoryTypeResponseDto
	 */
	@Override
	public CategoryTypeResponseDto update(UpdateCategoryTypeDto updateCategoryTypeDto, Long id) {
		CategoryType categoryType = findEntity(id, null);
		log.info("CategoryType [{}] updates via the following UpdateCategoryTypeDto [{}]", categoryType,
				updateCategoryTypeDto);

		CategoryTypeVt active = effectivityService.getActiveVt(categoryType.getValidTimes(), LocalDate.now());
		double allocPct = updateCategoryTypeDto.getBudgetAllocationPercentage() != null
				? updateCategoryTypeDto.getBudgetAllocationPercentage()
				: active.getBudgetAllocationPercentage();
		double budgetAmt = updateCategoryTypeDto.getAmountAllocated() != null
				? updateCategoryTypeDto.getAmountAllocated()
				: active.getBudgetAmount();
		double savedAmt = updateCategoryTypeDto.getSavedAmount() != null ? updateCategoryTypeDto.getSavedAmount()
				: active.getSavedAmount();

		CategoryTypeVt updateVt = CategoryTypeVt.builder()
				.categoryType(categoryType)
				.name(active.getName())
				.budgetAllocationPercentage(allocPct)
				.budgetAmount(budgetAmt)
				.savedAmount(savedAmt)
				.build();

		effectivityService.applyVtUpdate(categoryType.getValidTimes(), updateVt, LocalDate.now());

		CategoryType saved = repository.save(categoryType);
		CategoryTypeVt activeVt = effectivityService.getActiveVt(saved.getValidTimes(), LocalDate.now());
		return categoryTypeMapper.toResponseDto(saved, activeVt);
	}

	@Override
	public CategoryTypeResponseDto get(Long categoryTypeId, LocalDate asOf) {
		CategoryType ct = findEntity(categoryTypeId, asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		CategoryTypeVt activeVt = effectivityService.getActiveVt(ct.getValidTimes(), target);
		return categoryTypeMapper.toResponseDto(ct, activeVt);
	}

	/**
	 * Function to fetch a particular CategoryType as of date
	 *
	 * @param categoryTypeId
	 *                       - ID corresponding to CategoryType to be fetched
	 * @param asOf
	 *                       - Point-in-time date
	 * @return
	 *         - Fetched CategoryType entity
	 */
	@Override
	public CategoryType findEntity(Long categoryTypeId, LocalDate asOf) {
		log.info("Reading CategoryType with id [{}] asOf [{}]", categoryTypeId, asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return repository.findByCategoryTypeIdAndAsOf(categoryTypeId, target).orElseThrow(
				() -> (new RuntimeException("Invalid category type id: " + categoryTypeId)));
	}

	/**
	 * Functionality to retrieve a user's CategoryType corresponding to a particular
	 * name as of date
	 *
	 * @param categoryTypeName
	 *                         - CategoryType name to fetch
	 * @param asOf
	 *                         - Point-in-time date
	 * @return
	 *         - retrieved CategoryType
	 */
	@Override
	public CategoryType findEntityByName(String categoryTypeName, LocalDate asOf) {
		User currentAuthUser = userService.getCurrentAuthUser();
		return findEntityByName(categoryTypeName, currentAuthUser, asOf);
	}

	/**
	 * Functionality to retrieve a user's CategoryType corresponding to a particular
	 * name as of date
	 *
	 * @param categoryTypeName
	 *                         - CategoryType name to fetch
	 * @param user
	 *                         - User to fetch CategoryType by name for
	 * @param asOf
	 *                         - Point-in-time date
	 * @return
	 *         - retrieved CategoryType
	 */
	@Override
	public CategoryType findEntityByName(String categoryTypeName, User user, LocalDate asOf) {
		long userId = user.getUserId();
		String normalCaseType = GeneralUtil.toNormalCase(categoryTypeName);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		log.info("Attempting to fetch Category Type with the name {} for User {} asOf [{}]", normalCaseType, userId,
				target);
		return repository.findByNameAndUserUserIdAndAsOf(normalCaseType, userId, target).orElse(null);
	}

	/**
	 * Function to remove a particular CategoryType from our database
	 *
	 * @param categoryTypeId
	 *                       - ID corresponding to CategoryType to be deleted
	 */
	@Override
	public void delete(Long categoryTypeId) {
		log.info("Deleting Category Type with id [{}]", categoryTypeId);
		CategoryType categoryType = findEntity(categoryTypeId, null);
		categoryType.setEndDate(LocalDate.now().minusDays(1));
		repository.save(categoryType);
	}

}
