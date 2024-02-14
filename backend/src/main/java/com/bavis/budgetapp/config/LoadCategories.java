package com.bavis.budgetapp.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bavis.budgetapp.dao.ParentCategoryRepository;
import com.bavis.budgetapp.model.Category;
import com.bavis.budgetapp.model.ParentCategory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadCategories{
	private final YAMLConfig yaml;
	private final ParentCategoryRepository categoryRepository;
	private static Logger LOG = LoggerFactory.getLogger(LoadCategories.class);

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			for(Map.Entry<String, Double> entry: yaml.getCategories().entrySet()) {
				//Determine If Category Already Exists
				Category cat  = categoryRepository.findByName(entry.getKey());
				if(cat != null) continue; //don't re-save
				
				//Save Category
				LOG.info("Category being saved : " + entry.getKey());
				ParentCategory newCategory = ParentCategory.builder()
						.budgetAllocation(entry.getValue())
						.build();
				newCategory.setName(entry.getKey());
				categoryRepository.save(newCategory);
			}
			
		};
	}
}