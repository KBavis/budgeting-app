package com.bavis.budgetapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 
 * @author bavis
 * 	
 * 		Class To Store Relationship Between Parent Categories (Wants, Needs, Investments) And Sub Categories
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder 
public class Category {
	@Id @JsonProperty("categoryId") @GeneratedValue private Long categoryId;
	private String name;
	private double budgetAllocation; //mainly for parent category, but could also be used for sub
	
	/**
	 * Many Categories For One Parent Category
	 */
	@ManyToOne
	@JoinColumn(name = "parentCategoryId")
	private Category parentCategory;
	
	/**
	 * One Category For Many Sub Categoreis
	 */
	@OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
	private List<Category> subCategories = new ArrayList<>();
	
	/**
	 * Many Categories For One User
	 */
	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Objects.equals(categoryId, other.categoryId) && Objects.equals(name, other.name);
	}
	
	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", name=" + name + "]";
	}
}
