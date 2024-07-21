package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.EditCategoryDto;
import com.bavis.budgetapp.dto.RenameCategoryDto;
import com.bavis.budgetapp.validator.group.BulkCategoryDtoValidationGroup;
import com.bavis.budgetapp.validator.group.CategoryDtoValidationGroup;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.service.CategoryService;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 *  Controller for creating/deleting/manipulating user Categories
 */
@RestController
@Log4j2
@RequestMapping("/category")
public class CategoryController {
	private final CategoryService _categoryService;

	public CategoryController(CategoryService _categoryService) {
		this._categoryService = _categoryService;
	}
	/**
	 * Bulk Create Categories of a same CategoryType
	 *
	 * @param categories
	 * 			- List of CategoryDtos pertaining to same CategoryType
	 * @return
	 * 			- Saved List of Categories
	 */
	@PostMapping("/bulk")
	public List<Category> bulkCreate(@RequestBody @Validated({CategoryDtoValidationGroup.class, BulkCategoryDtoValidationGroup.class}) BulkCategoryDto categories){
		log.info("Received request to create categories: [{}]", categories.toString());
		return _categoryService.bulkCreate(categories);
	}

	/**
	 * Create a single category for a user
	 *
	 * @param addCategoryDto
	 * 			- DTO containing new Category and updates to any existing Categories
	 * @return
	 * 			- created Category
	 */
	@PostMapping
	public Category create(@RequestBody @Valid AddCategoryDto addCategoryDto) {
		log.info("Received Category creation request via following AddCategoryDto: [{}]", addCategoryDto);
		return _categoryService.create(addCategoryDto);
	}

	/**
	 * Read a specified Category
	 *
	 * @param categoryId
	 * 			- Category ID pertaining to specified Category to retrieve
	 * @return
	 * 			- Fetched Category from DB
	 */
	@GetMapping("/{categoryId}")
	public Category read(@PathVariable(value = "categoryId") Long categoryId) {
		log.info("Received Category read request for [{}]", categoryId);
		
		try {
			return _categoryService.read(categoryId);
		}  catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find category with id " + categoryId);
		}
	}

	/**
	 * Fetch all available Categories for authenticated user
	 *
	 * @return
	 * 		- all available Categories
	 */
	@GetMapping
	public List<Category> readAll() {
		log.info("Received request to fetch all Categories for current authenticated user");
		return _categoryService.readAll();
	}

	/**
	 * Edit an existing Category
	 *
	 * @param editCategoryDto
	 * 			- DTO containing updated Category budget allocations
	 * @return
	 * 			- Updated Categories
	 */
	@PutMapping
	public List<Category> updateCategoryAllocations(@RequestBody EditCategoryDto editCategoryDto) {
		log.info("Received update Category allocations request via EditCategoryDto [{}]", editCategoryDto);
		return _categoryService.updateCategoryAllocations(editCategoryDto);
	}

	/**
	 * Delete a particular Category
	 *
	 * @param categoryId
	 * 			- CategoryID pertaining to Category needing deletion
	 */
	@DeleteMapping("/{categoryId}")
	public void delete(@PathVariable(value = "categoryId") Long categoryId) {
		log.info("Received Category delete request for id [{}]", categoryId);
		_categoryService.delete(categoryId);
	}


	/**
	 * Endpoint to rename a specific Category
	 *
	 * @param renameCategoryDto
	 * 			- DTO containing updated Category name and CategoryId
	 */
	@PutMapping("/rename")
	public Category renameCategory(@RequestBody @Valid RenameCategoryDto renameCategoryDto) {
		log.info("Received rename Category request via RenameCategoryDto [{}]", renameCategoryDto);
		return _categoryService.renameCategory(renameCategoryDto);
	}
}
