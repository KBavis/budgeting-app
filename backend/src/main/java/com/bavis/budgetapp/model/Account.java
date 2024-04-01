package com.bavis.budgetapp.model;

import com.bavis.budgetapp.enumeration.AccountType;

import jakarta.persistence.*;
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
	private String accountId;

	@Column(nullable = false)
	private String accountName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountType accountType;

	@Column(nullable = false)
	private double balance;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
	private Connection connection;
}