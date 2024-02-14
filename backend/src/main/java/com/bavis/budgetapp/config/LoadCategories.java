package com.bavis.budgetapp.config;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bavis.budgetapp.dao.CategoryRepository;
import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.model.ParentCategory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadCategories{
	private final YAMLConfig yaml;
	private final CategoryRepository categoryRepository;
	private static Logger LOG = LoggerFactory.getLogger(LoadCategories.class);

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			for(Map.Entry<String, Double> entry: yaml.getCategories().entrySet()) {
				//Determine If Category Already Exists
				Category cat  = categoryRepository.findByName(entry.getKey());
				if(cat != null) continue; //don't re-save
				
				//Save Parent Category
				LOG.info("Category being saved : " + entry.getKey());
				Category newCategory = Category.builder()
						.budgetAllocation(entry.getValue())
						.name(entry.getKey())
						.parentCategory(null) //no parent for parent category
						.user(null) //TODO: Configure this so that it's the authenticated user once JWT set up 
						.subCategories(new ArrayList<Category>()) //allow users to add sub categories
						.build();
				categoryRepository.save(newCategory);
			}
			
		};
	}
}