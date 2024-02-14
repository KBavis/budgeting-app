package com.bavis.budgetapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubCategory extends Category{
	/**
	 * One User, But User Can Be Associated With Multiple Sub Categories;
	 */
	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "parentCategoryId", nullable = false)
	private ParentCategory category;

}
