package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bavis.budgetapp.entity.CategoryVt;

/**
 * @author Kellen Bavis
 * 
 * DAO for working with CategoryVt entities
 */
public interface CategoryVtRepository extends JpaRepository<CategoryVt, Long> {
}
