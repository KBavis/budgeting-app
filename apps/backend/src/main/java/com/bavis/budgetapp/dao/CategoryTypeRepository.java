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

	@Query("""
			SELECT DISTINCT ct FROM CategoryType ct 
			JOIN ct.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
			WHERE ct.categoryTypeId = :categoryTypeId 
			  AND ct.startDate <= :asOf AND ct.endDate >= :asOf AND ct.startDate <= ct.endDate
			""")
	Optional<CategoryType> findByCategoryTypeIdAndAsOf(@Param("categoryTypeId") Long categoryTypeId, @Param("asOf") LocalDate asOf);

	@Query("""
			SELECT DISTINCT ct FROM CategoryType ct 
			JOIN ct.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
			WHERE vt.name = :categoryTypeName AND ct.user.userId = :userId 
			  AND ct.startDate <= :asOf AND ct.endDate >= :asOf AND ct.startDate <= ct.endDate
			""")
	Optional<CategoryType> findByNameAndUserUserIdAndAsOf(@Param("categoryTypeName") String categoryTypeName, @Param("userId") long userId, @Param("asOf") LocalDate asOf);

	@Query("""
			SELECT DISTINCT ct FROM CategoryType ct 
			JOIN ct.validTimes vt ON vt.startDate <= :asOf AND vt.endDate >= :asOf 
			WHERE ct.user.userId = :userId 
			  AND ct.startDate <= :asOf AND ct.endDate >= :asOf AND ct.startDate <= ct.endDate
			""")
	List<CategoryType> findByUserUserIdAndAsOf(@Param("userId") Long userId, @Param("asOf") LocalDate asOf);
}
