package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.request.AddCategoryDto;
import com.bavis.budgetapp.dto.request.BulkCategoryDto;
import com.bavis.budgetapp.dto.request.EditCategoryDto;
import com.bavis.budgetapp.dto.request.RenameCategoryDto;
import com.bavis.budgetapp.dto.response.CategoryResponseDto;
import com.bavis.budgetapp.entity.Category;

import java.time.LocalDate;
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
	 * 			- List of created CategoryResponseDtos
	 */
	List<CategoryResponseDto> bulkCreate(BulkCategoryDto categories);

	/**
	 * Function to create a single Category entity
	 *
	 * @param addCategoryDto
	 * 			- DTO used to create new Category and update existing Category allocations
	 * @return
	 * 			- Created CategoryResponseDto
	 */
	CategoryResponseDto create(AddCategoryDto addCategoryDto);

	/**
	 * Function to update Category allocations
	 *
	 * @param editCategoryDto
	 * 			- DTO containing updated category allocations
	 * @return
	 * 			- Updated CategoryResponseDtos
	 */
	List<CategoryResponseDto> updateCategoryAllocations(EditCategoryDto editCategoryDto);

	/**
	 * Function to rename a Category
	 *
	 * @param renameCategoryDto
	 * 			- DTO containing CategoryId to update and updated name
	 * @return
	 * 			- updated CategoryResponseDto
	 */
	CategoryResponseDto renameCategory(RenameCategoryDto renameCategoryDto);

	/**
	 * Function to fetch a specific Category as of a point-in-time date mapped to response DTO
	 *
	 * @param categoryId
	 * 			- Category ID corresponding to specific Category to be fetched
	 * @param asOf
	 * 			- Point-in-time evaluation date (defaults to today if null)
	 * @return
	 * 			- fetched CategoryResponseDto
	 */
	CategoryResponseDto get(Long categoryId, LocalDate asOf);

	/**
	 * Function to fetch a specific Category entity as of a point-in-time date
	 *
	 * @param categoryId
	 * 			- Category ID corresponding to specific Category to be fetched
	 * @param asOf
	 * 			- Point-in-time evaluation date (defaults to today if null)
	 * @return
	 * 			- fetched Category entity
	 */
	Category findEntity(Long categoryId, LocalDate asOf);

	/**
	 * Function to fetch all Category entities pertaining to authenticated user as of a point-in-time date
	 *
	 * @param asOf
	 * 			- Point-in-time evaluation date (defaults to today if null)
	 * @return
	 * 		- all Categories corresponding to auth user as of date
	 */
	List<Category> findAllEntities(LocalDate asOf);

	/**
	 * Function to fetch all CategoryResponseDtos pertaining to authenticated user as of a point-in-time date
	 *
	 * @param asOf
	 * 			- Point-in-time evaluation date (defaults to today if null)
	 * @return
	 * 		- all CategoryResponseDtos corresponding to auth user as of date
	 */
	List<CategoryResponseDto> getAll(LocalDate asOf);

	/**
	 * Function to delete a specific Category
	 *
	 * @param categoryId
	 * 			- Category ID corresponding to specific Category to be deleted
	 */
	void delete(Long categoryId);
}
