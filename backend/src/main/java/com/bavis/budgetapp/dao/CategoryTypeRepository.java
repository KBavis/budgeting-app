package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.entity.CategoryType;

/**
 * @author Kellen Bavis
 *
 *  DAO for working with CategoryType entities
 */
public interface CategoryTypeRepository extends JpaRepository<CategoryType, Long> {

	/**
	 * Fetch CategoryType based on passed in name
	 *
	 * @param categoryTypeName
	 * 			- specified name to search for
	 * @return
	 * 			- CategoryType pertaining to specified name
	 */
	CategoryType findByName(String categoryTypeName);
}
