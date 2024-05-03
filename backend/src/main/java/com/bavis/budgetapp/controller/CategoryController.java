package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.validator.group.BulkCategoryDtoValidationGroup;
import com.bavis.budgetapp.validator.group.CategoryDtoValidationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
	private final CategoryService categoryService;
	private static Logger LOG = LoggerFactory.getLogger(CategoryController.class);


	/**
	 * Controller Method to Bulk Create Categories of a same CategoryType
	 *
	 * @param categories
	 * 			- List of CategoryDto's pertaining to same CategoryType
	 * @return
	 * 			- Saved List of Categories
	 */
	@PostMapping("/bulk")
	public List<Category> bulkCreate(@RequestBody @Validated({CategoryDtoValidationGroup.class, BulkCategoryDtoValidationGroup.class}) BulkCategoryDto categories){
		LOG.info("Received request to create categories: [{}]", categories.toString());
		return categoryService.bulkCreate(categories);
	}

	@PostMapping("/{categoryTypeId}")
	public Category create(@RequestBody Category category, @PathVariable(value = "categoryTypeId") Long categoryTypeId) {
		LOG.info("Recieved Category creation request for [{}] within the category type [{}]", category, categoryTypeId);
		
		try {
			return categoryService.create(category, categoryTypeId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to create parent category");
		}
	}
	
	@GetMapping("/{categoryId}")
	public Category read(@PathVariable(value = "categoryId") Long categoryId) {
		LOG.info("Recieved Category read request for [{}]", categoryId);
		
		try {
			return categoryService.read(categoryId);
		}  catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find category with id " + categoryId);
		}
	}
	
	@PutMapping("/{categoryId}")
	public Category update(@PathVariable(value = "categoryId") Long categoryId, @RequestBody Category category) {
		LOG.info("Recieved Category update request for id [{}] and category [{}]", categoryId, category);
		return categoryService.update(category, categoryId);
	}
	
	@DeleteMapping("/{categoryId}")
	public void delete(@PathVariable(value = "categoryId") Long categoryId) {
		LOG.info("Recieved Category delete request for id [{}]", categoryId);
		categoryService.delete(categoryId);
	}
}
