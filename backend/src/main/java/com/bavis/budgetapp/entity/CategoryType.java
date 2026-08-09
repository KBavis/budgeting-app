package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bavis.budgetapp.constants.TemporalConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Kellen Bavis
 * 	
 * Entity To Store Relationship Between Parent Category Types (Wants, Needs, Investments) and User
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

	@Builder.Default
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = TemporalConstants.BEGINNING_OF_TIME;

	@Builder.Default
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate = TemporalConstants.END_OF_TIME;

	@ManyToOne
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;
	
	@Builder.Default
	@OneToMany(mappedBy = "categoryType", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<CategoryTypeVt> validTimes = new ArrayList<>();

	@Transient
	@Builder.Default
	private List<Category> categories = new ArrayList<>();
}
