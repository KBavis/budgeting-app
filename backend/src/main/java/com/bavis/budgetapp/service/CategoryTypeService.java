package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
import com.bavis.budgetapp.entity.CategoryType;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Service to house functionality regarding CategoryType entities
 */
public interface CategoryTypeService {
	/**
	 * Function to create a Category Type entity
	 *
	 * @param categoryType
	 * 			- Category Type to be persisted within our database
	 * @return
	 * 			- Saved Category Type
	 */
	CategoryType create(CategoryType categoryType);

	/**
	 * Functionality to read all Categories pertaining to the authenticated user
	 *
	 * @return
	 * 		- all Category entities corresponding to authenticated user
	 */
	List<CategoryType> readAll();

	/**
	 * Function to create multiple Category Type entities in a singular request
	 *
	 * @param categoryTypeDtos
	 * 			- List of Category Type DTOs utilized to persist CategoryType entities
	 * @return
	 * 			- List of persisted CategoryType entities
	 */
	List<CategoryType> createMany(List<CategoryTypeDto> categoryTypeDtos);

	/**
	 * Function to update a CategoryType with updated properties
	 *
	 * @param updateCategoryTypeDto
	 * 			- DTO containing relevant CategoryType updates
	 * @param id
	 * 			- ID corresponding to CategoryType to be updated
	 * @return
	 * 			- Updated and saved CategoryType entity
	 */
	CategoryType update(UpdateCategoryTypeDto updateCategoryTypeDto, Long id);

	/**
	 * Function to fetch a particular CategoryType from our database
	 *
	 * @param categoryTypeId
	 * 			- ID corresponding to CategoryType to be fetched
	 * @return
	 * 			- Fetched CategoryType entity
	 */
	CategoryType read(Long categoryTypeId);

	/**
	 * Function to remove a particular CategoryType from our database
	 *
 	 * @param categoryTypeId
	 * 			- ID corresponding to CategoryType to be deleted
	 */
	void delete(Long categoryTypeId);
}
