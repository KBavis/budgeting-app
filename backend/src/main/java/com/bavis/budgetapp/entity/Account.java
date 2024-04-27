package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.constants.AccountType;

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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "connectionId", referencedColumnName = "connectionId")
	private Connection connection;
}