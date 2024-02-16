package com.bavis.budgetapp.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class CategoryType {
	@Id @JsonProperty("categoryTypeId") @GeneratedValue private Long categoryTypeId;
	private String name;
	private double budgetAllocation; //mainly for parent category, but could also be used for sub
	
	@Builder.Default
	@OneToMany(mappedBy = "categoryType", cascade = CascadeType.ALL)
	private List<Category> categories = new ArrayList<>();
}
