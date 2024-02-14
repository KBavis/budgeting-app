package com.bavis.budgetapp.controller;

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

import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
	private final CategoryService categoryService;
	private static Logger LOG = LoggerFactory.getLogger(CategoryController.class);
	
	@PostMapping
	public Category create(@RequestBody Category category) {
		LOG.debug("Recieved Category creation request for [{}]", category);
		
		try {
			return categoryService.create(category);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to create parent category");
		}
	}
	
	@GetMapping("/{categoryId}")
	public Category read(@PathVariable(value = "categoryId") Long categoryId) {
		LOG.debug("Recieved Category read request for [{}]", categoryId);
		
		try {
			return categoryService.read(categoryId);
		}  catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find category with id " + categoryId);
		}
	}
	
	@PutMapping("/{categoryId}")
	public Category update(@PathVariable(value = "categoryId") Long categoryId, @RequestBody Category category) {
		LOG.debug("Recieved Category update request for id [{}] and category [{}]", categoryId, category);
		return categoryService.update(category);
	}
	
	@DeleteMapping("/{categoryId}")
	public void delete(@PathVariable(value = "categoryId") Long categoryId) {
		LOG.debug("Recieved Category delete request for id [{}]", categoryId);
		categoryService.delete(categoryId);
	}
}
