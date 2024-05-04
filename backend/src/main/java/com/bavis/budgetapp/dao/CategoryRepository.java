package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.entity.Category;

/**
 * @author Kellen Bavis
 *
 * DAO for working with Category entities
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

	/**
	 * Fetch Category by specified ID
	 *
	 * @param categoryId
	 * 			- Category ID pertaining to category needing to be fetched
	 * @return
	 * 			- fetched Category pertaining to ID passed in
	 */
	Category findByCategoryId(Long categoryId);

	/**
	 * Fetch Category by a specific name
	 *
	 * @param categoryName
	 * 			- name to search for Categories by
	 * @return
	 * 			- Category pertaining to specified name
	 */
	Category findByName(String categoryName);
}

