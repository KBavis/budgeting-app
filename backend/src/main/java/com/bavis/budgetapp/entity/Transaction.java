package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * @author Kellen Bavis
 * 
 *  Transaction Entity To Hold Information Regarding What User Spends Money On
 *
 */
@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
	@Id
	private String transactionId;
	private String name;
	private double amount;
	private LocalDate date;
	
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
	@Override
	public String toString() {
		return "Transaction [name=" + name + ", amount=" + amount + ", category=" + category + ", date=" + date
				+ ", accountSource=" + account + "]";
	}
	
}
