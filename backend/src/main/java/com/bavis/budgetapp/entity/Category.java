package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Kellen Bavis
 * 	
 * Entity to Store Relationship Between Base Category and User
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class Category {
	@Id @JsonProperty("categoryId") @GeneratedValue
	private Long categoryId;

	@Builder.Default
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = TemporalConstants.BEGINNING_OF_TIME;

	@Builder.Default
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate = TemporalConstants.END_OF_TIME;

	/**
	 * This Category Will Be Created By One Individual User
	 */
	@ManyToOne
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;

	@Builder.Default
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<CategoryVt> validTimes = new ArrayList<>();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Objects.equals(categoryId, other.categoryId) && Objects.equals(user, other.user);
	}
}
