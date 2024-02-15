package com.bavis.budgetapp.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@ManyToOne
	@JoinColumn(name = "categoryTypeId")
	@JsonIgnoreProperties("categories") //needed to prevent ciruclar dependencies
	private CategoryType categoryType;

	
	/**
	 * This Category Will Be Created By One Individaul User
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
		return Objects.equals(categoryId, other.categoryId) && Objects.equals(categoryType, other.categoryType)
				&& Objects.equals(name, other.name) && Objects.equals(user, other.user);
	}

	
	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", name=" + name + ", categoryType=" + categoryType + ", user="
				+ user + "]";
	}
}
