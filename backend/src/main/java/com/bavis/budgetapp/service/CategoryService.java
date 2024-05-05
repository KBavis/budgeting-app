package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.entity.Category;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Service to house functionality regarding Category entities
 */
public interface CategoryService {
	/**
	 * Function to create multiple Category entities at once
	 *
	 * @param categories
	 * 			- DTO to store information needed to create multiple Category entities
	 * @return
	 * 			- List of created Category entities
	 */
	List<Category> bulkCreate(BulkCategoryDto categories);

	/**
	 * Function to create a single Category entity
	 *
	 * @param category
	 * 			- Category to be persisted within our database
	 * @param categoryTypeId
	 * 			- CategoryType entity ID to associate Category to
	 * @return
	 * 			- Created Category entity
	 * @throws Exception
	 * 			- Thrown in the case that an error occurs while creating a Category
	 */
	Category create(Category category, Long categoryTypeId) throws Exception;

	/**
	 * Function to update a Category
	 *
	 * @param category
	 * 			- Category with updated properties
	 * @param id
	 * 			- ID of corresponding Category entity to be updated
	 * @return
	 * 			- Updated Category
	 */
	Category update(Category category, Long id);

	/**
	 * Function to fetch a specific Category
	 *
	 * @param categoryId
	 * 			- Category ID corresponding to specific Category to be fetched
	 * @return
	 * 			- fetched Category
	 */
	Category read(Long categoryId);

	/**
	 * Function to delete a specific Category
	 *
	 * @param categoryId
	 * 			- Category ID corresponding to specific Category to be deleted
	 */
	void delete(Long categoryId);
}
