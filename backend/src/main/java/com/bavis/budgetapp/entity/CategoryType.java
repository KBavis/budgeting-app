package com.bavis.budgetapp.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Kellen Bavis
 * 	
 * 	Entity To Store Relationship Between Parent Categories (Wants, Needs, Investments) And Sub Categories
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder 
public class CategoryType {
	@Id @JsonProperty("categoryTypeId") @GeneratedValue
	private Long categoryTypeId;

	private String name;

	private double budgetAllocationPercentage;

	private double budgetAmount;

	@ManyToOne
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;
	
	@Builder.Default
	@OneToMany(mappedBy = "categoryType", cascade = CascadeType.ALL)
	private List<Category> categories = new ArrayList<>();
}
