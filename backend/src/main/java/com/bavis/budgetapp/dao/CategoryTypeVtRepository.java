package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bavis.budgetapp.entity.CategoryTypeVt;

/**
 * @author Kellen Bavis
 * 
 * DAO for working with CategoryTypeVt entities
 */
public interface CategoryTypeVtRepository extends JpaRepository<CategoryTypeVt, Long> {
}
