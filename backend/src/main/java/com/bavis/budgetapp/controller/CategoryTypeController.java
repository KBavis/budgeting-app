package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.request.CategoryTypeDto;
import com.bavis.budgetapp.dto.request.UpdateCategoryTypeDto;
import com.bavis.budgetapp.dto.response.CategoryTypeResponseDto;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.validator.group.UpdateCategoryTypeDtoValidationGroup;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Controller utilized for manipulating/reading CategoryTypes for users
 */

@RestController
@Log4j2
@RequestMapping("/category-type")
public class CategoryTypeController {

	private final CategoryTypeService _categoryTypeService;

	public CategoryTypeController(CategoryTypeService _categoryTypeService) {
		this._categoryTypeService = _categoryTypeService;
	}

	/**
	 * Endpoint utilized for creating CategoryTypes
	 *
	 * @param categoryType
	 * 			- CategoryType to be created
	 * @return
	 * 			- Created CategoryTypeResponseDto
	 */
	@PostMapping
	public CategoryTypeResponseDto createCategoryType(@RequestBody CategoryTypeDto categoryTypeDto) {
		log.info("Received Category Type creation request for [{}]", categoryTypeDto);
		try {
			return _categoryTypeService.create(categoryTypeDto);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to create category type");
		}
	}

	/**
	 * Endpoint to bulk create CategoryTypes
	 *
	 * @param categoryTypes
	 * 			- List of CategoryTypes to create
	 * @return
	 * 			- List of created CategoryTypeResponseDtos
	 */
	@PostMapping("/bulk")
	public List<CategoryTypeResponseDto> createManyCategoryTypes(@RequestBody List<CategoryTypeDto> categoryTypes) {
		log.info("Received request to create multiple CategoryTypes: [{}]", categoryTypes);
		return _categoryTypeService.createMany(categoryTypes);
	}

	/**
	 * Read all CategoryTypes pertaining to authenticated user
	 *
	 * @param asOf
	 * 			- Optional point-in-time date to evaluate CategoryType state history
	 * @return
	 * 			- List of CategoryTypeResponseDtos associated with authenticated user
	 */
	@GetMapping
	public List<CategoryTypeResponseDto> readMany(@RequestParam(name = "asOf", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		log.info("Received request to read all Category Types for the authenticated user with asOf [{}]", asOf);
		return _categoryTypeService.getAll(asOf);
	}

	/**
	 * Read a single CategoryType pertaining to authenticated user
	 *
	 * @param categoryTypeId
	 * 			- CategoryTypeId to retrieve
	 * @param asOf
	 * 			- Optional point-in-time date to evaluate CategoryType state history
	 * @return
	 * 			- CategoryTypeResponseDto corresponding to CategoryTypeId
	 */
	@GetMapping("/{categoryTypeId}")
	public CategoryTypeResponseDto read(@PathVariable(value = "categoryTypeId") Long categoryTypeId,
										 @RequestParam(name = "asOf", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		log.info("Received CategoryType read request for Category Type with ID of {} and asOf [{}]", categoryTypeId, asOf);
		return _categoryTypeService.get(categoryTypeId, asOf);
	}

	/**
	 * Endpoint utilized for updating a CategoryType entity
	 *
	 * @param updateCategoryTypeDto
	 * 			- CategoryType attributes needing update
	 * @return
	 * 			- Updated CategoryTypeResponseDto
	 */
	@PutMapping("/{categoryTypeId}")
	public CategoryTypeResponseDto update(@PathVariable(value = "categoryTypeId") Long categoryTypeId, @RequestBody @Validated(UpdateCategoryTypeDtoValidationGroup.class) UpdateCategoryTypeDto updateCategoryTypeDto) {
		log.info("Received CategoryType update request for id [{}] and updates [{}]", categoryTypeId, updateCategoryTypeDto);
		return _categoryTypeService.update(updateCategoryTypeDto, categoryTypeId);
	}

	/**
	 * Endpoint utilized for deleting a CategoryType entity
	 *
	 * @param categoryTypeId
	 * 			- ID corresponding to CategoryType entity needing deletion
	 */
	@DeleteMapping("/{categoryTypeId}")
	public void delete(@PathVariable(value = "categoryTypeId") Long categoryTypeId) {
		log.info("Received CategoryType delete request for id [{}]", categoryTypeId);
		_categoryTypeService.delete(categoryTypeId);
	}
}
