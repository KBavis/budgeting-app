package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.EditCategoryDto;
import com.bavis.budgetapp.dto.RenameCategoryDto;
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
	 * @param addCategoryDto
	 * 			- DTO used to create new Category and update existing Category allocations
	 * @return
	 * 			- Created Category entity
	 */
	Category create(AddCategoryDto addCategoryDto);

	/**
	 * Function to update Category allocations
	 *
	 * @param editCategoryDto
	 * 			- DTO containing updated category allocations
	 * @return
	 * 			- Updated Categories
	 */
	List<Category> updateCategoryAllocations(EditCategoryDto editCategoryDto);


	/**
	 * Function to rename a Category
	 *
	 * @param renameCategoryDto
	 * 			- DTO containing CategoryId to update and updated name
	 * @return
	 * 			- updated Category
	 */
	Category renameCategory(RenameCategoryDto renameCategoryDto);

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
	 * Function to fetch all Categories pertaining to authenticated user
	 *
	 * @return
	 * 		- all Categories corresponding to auth user
	 */
	List<Category> readAll();

	/**
	 * Function to delete a specific Category
	 *
	 * @param categoryId
	 * 			- Category ID corresponding to specific Category to be deleted
	 */
	void delete(Long categoryId);
}
