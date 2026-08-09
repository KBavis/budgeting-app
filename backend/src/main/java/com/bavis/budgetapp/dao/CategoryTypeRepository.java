package com.bavis.budgetapp.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	 * @param userId
	  *			- userId to fetch CategoryType for
	 * @return
	 * 			- CategoryType pertaining to specified name
	 */
	CategoryType findByNameAndUserUserId(String categoryTypeName, long userId);

	List<CategoryType> findByUserUserId(Long id);
}
