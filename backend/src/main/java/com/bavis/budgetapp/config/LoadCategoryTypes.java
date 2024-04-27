package com.bavis.budgetapp.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bavis.budgetapp.dao.CategoryTypeRepository;
import com.bavis.budgetapp.entity.CategoryType;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadCategoryTypes{
	private final YAMLConfig yaml;
	private final CategoryTypeRepository repository;
	private static Logger LOG = LoggerFactory.getLogger(LoadCategoryTypes.class);

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			for(Map.Entry<String, Double> entry: yaml.getCategories().entrySet()) {
				//Determine If Category Type Already Exists
				CategoryType cat  = repository.findByName(entry.getKey());
				if(cat != null) continue; //don't re-save
				
				//Save Parent Category
				LOG.info("CategoryType being saved : " + entry.getKey());
				CategoryType categoryType = CategoryType.builder()
						.budgetAllocation(entry.getValue())
						.name(entry.getKey())
						.build();
				repository.save(categoryType);
			}
			
		};
	}
}