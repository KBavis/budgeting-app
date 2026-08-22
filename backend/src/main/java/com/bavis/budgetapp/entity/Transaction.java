package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.bavis.budgetapp.constants.TemporalConstants;
import com.bavis.budgetapp.dto.response.CategoryResponseDto;
import com.bavis.budgetapp.model.PlaidConfidenceLevel;
import com.bavis.budgetapp.model.PlaidDetailedCategory;
import com.bavis.budgetapp.model.PlaidPrimaryCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Kellen Bavis
 * 
 * Transaction Entity To Hold Information Regarding What User Spends Money On
 *
 */
@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

	@Embeddable
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Location {
		private String address;
		private String city;
		private String region;
		@Column(name = "postal_code")
		private String postalCode;
		private String country;
		private String lat; // latitude
		private String lon; // longitude
	}

	@Embeddable
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PersonalFinanceCategory {
		@Enumerated(EnumType.STRING)
		@Column(name = "plaid_confidence_level")
		private PlaidConfidenceLevel plaidConfidenceLevel;

		@Column(name = "plaid_primary_category")
		@Enumerated(EnumType.STRING)
		private PlaidPrimaryCategory primaryCategory;

		@Column(name = "plaid_detailed_category")
		@Enumerated(EnumType.STRING)
		private PlaidDetailedCategory detailedCategory;
	}

	private String merchantName;

	@Id
	private String transactionId;

	@Builder.Default
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = TemporalConstants.BEGINNING_OF_TIME;

	@Builder.Default
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate = TemporalConstants.END_OF_TIME;

	private String name;
	private double amount;
	private LocalDate date;
	private LocalDateTime dateTime;
	private String logoUrl;

	@Column(name = "updated_by_user", columnDefinition = "boolean default false")
	private boolean updatedByUser;

	@Embedded
	private Location location;

	@Embedded
	private PersonalFinanceCategory personalFinanceCategory;

	/**
	 * Suggested category based on Model prediction (JPA FK preserved for persistence)
	 */
	@ToString.Exclude
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "suggestedCategoryId", referencedColumnName = "categoryId")
	private Category suggestedCategory;

	/**
	 * Fully-resolved CategoryResponseDto for the suggestedCategory.
	 * Populated in the service layer via EffectivityService + CategoryMapper.
	 */
	@Transient
	@JsonProperty("suggestedCategory")
	private CategoryResponseDto suggestedCategoryDto;

	/**
	 * Many Transactions To One Account
	 */
	@ToString.Exclude
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "accountId", referencedColumnName = "accountId")
	private Account account;
	
	/**
	 * Many Transactions To One Category 
	 */
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
	private Category category;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return Objects.equals(account, other.account)
				&& Double.doubleToLongBits(amount) == Double.doubleToLongBits(other.amount)
				&& Objects.equals(category, other.category) && Objects.equals(date, other.date)
				&& Objects.equals(name, other.name);
	}
}

