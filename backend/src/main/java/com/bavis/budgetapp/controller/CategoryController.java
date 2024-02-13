package com.bavis.budgetapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bavis.budgetapp.dao.CateogryRepository;
import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CateogryRepository categoryRepository;
	private final CategoryService categoryService;
	private static Logger LOG = LoggerFactory.getLogger(CategoryController.class);
	
	@PostMapping
	public Category create(@RequestBody Category category) {
		LOG.debug("Recieved Category creation request for [{}]", category);
		return categoryService.create(category);
	}
	
	@GetMapping("/{categoryId}")
	public Category read(@PathVariable Long categoryId) {
		LOG.debug("Recieved Category read request for [{}]", categoryId);
		return categoryService.read(categoryId);
	}
	
	@PutMapping("/{categoryId}")
	public Category update(@PathVariable Long categoryId, @RequestBody Category category) {
		LOG.debug("Recieved Category update request for id [{}] and category [{}]", categoryId, category);
		return categoryService.update(category);
	}
	
	@DeleteMapping("/{categoryId}")
	public void delete(@PathVariable Long categoryId) {
		LOG.debug("Recieved Category delete request for id [{}]", categoryId);
		categoryService.delete(categoryId);
	}
}
