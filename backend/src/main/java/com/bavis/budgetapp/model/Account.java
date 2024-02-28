package com.bavis.budgetapp.model;

import com.bavis.budgetapp.enumeration.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
 *  	Entity To Relate an Account with a User
 *
 *  	TODO: Fetch Accounts By /development/accounts/get
 *  			Use this inform
 *
 */
@Entity
@Table(name = "account")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {

	@Id
	@Column(nullable = false)
	private Long accountId; //account id to be fetched from Plaid, not generated

	@Column(nullable = false)
	private String accountName; //account name fetched from Plaid (allow users to alter)
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountType accountType;

	@Column(nullable = false)
	private double balance;
	
	/**
	 * Many Users To One Account
	 */
	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;
	
	/**
	 * One Connection To One Account
	 */
	@OneToOne
	@JoinColumn(name = "connectionId", nullable = false, unique = true)
	private Connection connection;
}
