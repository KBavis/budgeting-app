package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.service.CategoryTypeService;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 *  Controller for working with our CategoryType entities
 */
@RestController
@Log4j2
@RequestMapping("/category/type")
public class CategoryTypeController {
	private final CategoryTypeService _categoryTypeService;

	public CategoryTypeController(CategoryTypeService _categoryTypeService) {
		this._categoryTypeService = _categoryTypeService;
	}

	/**
	 * Creates a new CategoryType
	 *
	 * @param categoryType
	 * 			- CategoryType to create
	 * @return
	 * 			- newly created CategoryType
	 */
	@PostMapping
	public CategoryType createCategoryType(@RequestBody CategoryType categoryType) {
		log.info("Received Category Type creation request for [{}]", categoryType);
		
		try {
			return _categoryTypeService.create(categoryType);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to category type");
		}
	}

	/**
	 * Creates multiple CategoryType's
	 *
	 * @param categoryTypes
	 * 			- CategoryTypes to create
	 * @return
	 * 			- newly created CategoryTypes
	 */
	@PostMapping("/bulk")
	public List<CategoryType> createManyCategoryTypes(@RequestBody List<CategoryTypeDto> categoryTypes) {
		log.info("Received request to create multiple CategoryTypes: [{}]", categoryTypes);
		return _categoryTypeService.createMany(categoryTypes);
	}

	@GetMapping
	public List<CategoryType> readMany() {
		log.info("Received request to read all Category Types for the authenticated user");
		return _categoryTypeService.readAll();
	}


	/**
	 * Read a particular CategoryType pertaining to passed in CategoryID
	 *
	 * @param categoryTypeId
	 * 			- specific CategoryID to fetch
	 * @return
	 * 			- fetched CategoryType
	 */
	@GetMapping("/{categoryTypeId}")
	public CategoryType read(@PathVariable(value = "categoryTypeId") Long categoryTypeId) {
		log.info("Received CategoryType read request for Category Type with ID of {}", categoryTypeId);
		
		try {
			return _categoryTypeService.read(categoryTypeId);
		}  catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find category with id " + categoryTypeId);
		}
	}


	/**
	 * Update a particular CategoryType
	 *
	 * @param categoryTypeId
	 * 			- CategoryId pertaining to CategoryType needing udpates
	 * @param categoryType
	 * 			- CategoryType with updates
	 * @return
	 * 			- updated CategoryType
	 */
	@PutMapping("/{categoryTypeId}")
	public CategoryType update(@PathVariable(value = "categoryTypeId") Long categoryTypeId, @RequestBody CategoryType categoryType) {
		log.info("Recieved CategoryType update request for id [{}] and category [{}]", categoryTypeId, categoryType);
		return _categoryTypeService.update(categoryType, categoryTypeId);
	}

	/**
	 * Delete CategoryType
	 *
	 * @param categoryTypeId
	 * 			- specific CategoryID pertaining to CategoryType needing deletion
	 */
	@DeleteMapping("/{categoryTypeId}")
	public void delete(@PathVariable(value = "categoryId") Long categoryTypeId) {
		log.info("Recieved CategoryType deletetion request for id [{}]", categoryTypeId);
		_categoryTypeService.delete(categoryTypeId);
	}
}
