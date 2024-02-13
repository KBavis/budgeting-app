package com.bavis.budgetapp.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 
 * @author bavis
 * 	
 * 		Entity To Seperate Transactions into Parent & Sub Categories 
 *
 */
@Entity
@Table(name = "category")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category {
	@Id @JsonProperty("categoryId") @GeneratedValue private Long categoryId;
	private String name;
	private double budgetAllocation;
	
	/**
	 * One User, But User Can Be Associated With Multiple Categories;
	 */
	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;
	
	/**
	 * One Parent Cateogory, But Parent Category Can Have Multiple Child Categories
	 * 
	 */
	@ManyToOne
	@JoinColumn(name = "parentCategoryId", nullable = false)
	private Category parentCategory;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Double.doubleToLongBits(budgetAllocation) == Double.doubleToLongBits(other.budgetAllocation)
				&& Objects.equals(name, other.name);
	}
	@Override
	public String toString() {
		return "Category [name=" + name + ", budgetAllocation=" + budgetAllocation + "]";
	}
	
	
}
