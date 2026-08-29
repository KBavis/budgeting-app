package com.bavis.budgetapp.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bavis.budgetapp.entity.Category;

/**
 * @author Kellen Bavis
 *
 * DAO for working with Category entities
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("""
			SELECT DISTINCT c FROM Category c 
			JOIN c.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
			WHERE c.categoryId = :categoryId 
			  AND c.startDate <= :asOf AND c.endDate >= :asOf AND c.startDate <= c.endDate
			""")
	Optional<Category> findByCategoryIdAndAsOf(@Param("categoryId") Long categoryId, @Param("asOf") LocalDate asOf);

	@Query("""
			SELECT DISTINCT c FROM Category c 
			JOIN c.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
			WHERE vt.name = :categoryName 
			  AND c.startDate <= :asOf AND c.endDate >= :asOf AND c.startDate <= c.endDate
			""")
	Optional<Category> findByNameAndAsOf(@Param("categoryName") String categoryName, @Param("asOf") LocalDate asOf);

	@Query("""
			SELECT DISTINCT c FROM Category c 
			JOIN c.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
			WHERE c.user.userId = :userId 
			  AND c.startDate <= :asOf AND c.endDate >= :asOf AND c.startDate <= c.endDate
			""")
	List<Category> findByUserUserIdAndAsOf(@Param("userId") Long userId, @Param("asOf") LocalDate asOf);

	@Query("""
			SELECT DISTINCT c FROM Category c 
			JOIN c.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
			WHERE vt.categoryType.categoryTypeId = :categoryTypeId 
			  AND c.startDate <= :asOf AND c.endDate >= :asOf AND c.startDate <= c.endDate
			""")
	List<Category> findByCategoryTypeIdAndAsOf(@Param("categoryTypeId") Long categoryTypeId, @Param("asOf") LocalDate asOf);
}
