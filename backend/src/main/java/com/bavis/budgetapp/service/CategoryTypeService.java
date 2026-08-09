package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.request.CategoryTypeDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.response.CategoryTypeResponseDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.User;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Service to house functionality regarding CategoryType entities
 */
public interface CategoryTypeService {
	/**
	 * Function to create a Category Type entity and return CategoryTypeResponseDto
	 *
	 * @param categoryType
	 * 			- Category Type to be persisted within our database
	 * @return
	 * 			- Saved CategoryTypeResponseDto
	 */
	CategoryTypeResponseDto create(CategoryType categoryType);

	/**
	 * Functionality to read all CategoryTypes pertaining to authenticated user as of a point-in-time date
	 *
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 		- all CategoryTypes corresponding to authenticated user as of date
	 */
	List<CategoryType> findAllEntities(LocalDate asOf);

	/**
	 * Functionality to read all CategoryTypeResponseDtos pertaining to authenticated user as of a point-in-time date
	 *
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 		- all CategoryTypeResponseDtos corresponding to authenticated user as of date
	 */
	List<CategoryTypeResponseDto> getAll(LocalDate asOf);

	/**
	 * Functionality to read all CategoryTypes pertaining to a specific User as of a point-in-time date
	 *
	 * @param user
	 * 			- User to fetch all CategoryTypes for
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 		- all CategoryTypes corresponding to user as of date
	 */
	List<CategoryType> findAllEntities(User user, LocalDate asOf);

	/**
	 * Functionality to retrieve a user's CategoryType corresponding to a particular name as of date
	 *
	 * @param categoryTypeName
	 * 			- CategoryType name to fetch
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 			- retrieved CategoryType
	 */
	CategoryType findEntityByName(String categoryTypeName, LocalDate asOf);

	/**
	 * Functionality to retrieve a user's CategoryType corresponding to a particular name as of date
	 *
	 * @param categoryTypeName
	 * 			- CategoryType name to fetch
	 * @param user
	 * 			- User to fetch CategoryType by name for
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 			- retrieved CategoryType
	 */
	CategoryType findEntityByName(String categoryTypeName, User user, LocalDate asOf);

	/**
	 * Function to create multiple Category Type entities in a singular request
	 *
	 * @param categoryTypeDtos
	 * 			- List of Category Type DTOs utilized to persist CategoryType entities
	 * @return
	 * 			- List of persisted CategoryTypeResponseDtos
	 */
	List<CategoryTypeResponseDto> createMany(List<CategoryTypeDto> categoryTypeDtos);

	/**
	 * Function to update a CategoryType with updated properties
	 *
	 * @param updateCategoryTypeDto
	 * 			- DTO containing relevant CategoryType updates
	 * @param id
	 * 			- ID corresponding to CategoryType to be updated
	 * @return
	 * 			- Updated and saved CategoryTypeResponseDto
	 */
	CategoryTypeResponseDto update(UpdateCategoryTypeDto updateCategoryTypeDto, Long id);

	/**
	 * Function to fetch a particular CategoryType Response DTO as of a point-in-time date
	 *
	 * @param categoryTypeId
	 * 			- ID corresponding to CategoryType to be fetched
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 			- Fetched CategoryTypeResponseDto
	 */
	CategoryTypeResponseDto get(Long categoryTypeId, LocalDate asOf);

	/**
	 * Function to fetch a particular CategoryType as of a point-in-time date
	 *
	 * @param categoryTypeId
	 * 			- ID corresponding to CategoryType to be fetched
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 			- Fetched CategoryType entity
	 */
	CategoryType findEntity(Long categoryTypeId, LocalDate asOf);

	/**
	 * Function to remove a particular CategoryType from our database
	 *
	 * @param categoryTypeId
	 * 			- ID corresponding to CategoryType to be deleted
	 */
	void delete(Long categoryTypeId);

	/**
	 * Function to remove a particular Category from a CategoryType entity
	 *
	 * @param category
	 * 			- Category to be removed
	 */
	void removeCategory(Category category);
}
