package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.bavis.budgetapp.model.PlaidConfidenceLevel;
import com.bavis.budgetapp.model.PlaidDetailedCategory;
import com.bavis.budgetapp.model.PlaidPrimaryCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * 
 * @author Kellen Bavis
 * 
 *  Transaction Entity To Hold Information Regarding What User Spends Money On
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
		private String lat; //latitude
		private String lon; //longitude
	}

	@Embeddable
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PersonalFinanceCategory {
		@Enumerated(EnumType.STRING)
		private PlaidConfidenceLevel plaidConfidenceLevel;
		@Enumerated(EnumType.STRING)
		private PlaidPrimaryCategory primaryCategory;
		@Enumerated(EnumType.STRING)
		private PlaidDetailedCategory detailedCategory;
	}

	private String merchantName;

	@Id
	private String transactionId;
	private String name;
	private double amount;
	private LocalDate date;
	private LocalDateTime dateTime;
	private String logoUrl;

	@Column(name = "is_deleted", columnDefinition = "boolean default false")
	private boolean isDeleted;

	@Column(name = "updated_by_user", columnDefinition = "boolean default false")
	private boolean updatedByUser;

	@Embedded
	private Location location;

	@Embedded
	private PersonalFinanceCategory personalFinanceCategory;



	/**
	 * Many Transactions To One Account
	 */
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "accountId", referencedColumnName = "accountId")
	private Account account;
	
	/**
	 * Many Transactions To One Category 
	 */
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
