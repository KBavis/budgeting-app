package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category/type")
public class CategoryTypeController {
	private final CategoryTypeService service;
	private static final Logger LOG = LoggerFactory.getLogger(CategoryTypeController.class);
	
	@PostMapping
	public CategoryType createCategoryType(@RequestBody CategoryType categoryType) {
		LOG.info("Received Category Type creation request for [{}]", categoryType);
		
		try {
			return service.create(categoryType);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to category type");
		}
	}

	@PostMapping("/bulk")
	public List<CategoryType> createManyCategoryTypes(@RequestBody List<CategoryTypeDto> categoryTypes) {
		LOG.info("Received request to create multiple CategoryTypes: [{}]", categoryTypes);
		return service.createMany(categoryTypes);
	}


	@GetMapping("/{categoryTypeId}")
	public CategoryType read(@PathVariable(value = "categoryTypeId") Long categoryTypeId) {
		LOG.info("Received CategoryType read request for Category Type with ID of {}", categoryTypeId);
		
		try {
			return service.read(categoryTypeId);
		}  catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find category with id " + categoryTypeId);
		}
	}

	@GetMapping
	public List<CategoryType> readAll(){
		LOG.info("Received CategoryType read many request");
		return service.readMany();
	}
	
	@PutMapping("/{categoryTypeId}")
	public CategoryType update(@PathVariable(value = "categoryTypeId") Long categoryTypeId, @RequestBody CategoryType categoryType) {
		LOG.info("Recieved CategoryType update request for id [{}] and category [{}]", categoryTypeId, categoryType);
		return service.update(categoryType, categoryTypeId);
	}
	
	@DeleteMapping("/{categoryTypeId}")
	public void delete(@PathVariable(value = "categoryId") Long categoryTypeId) {
		LOG.info("Recieved CategoryType deletetion request for id [{}]", categoryTypeId);
		service.delete(categoryTypeId);
	}
}
