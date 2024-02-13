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
 */
@Entity
@Table(name = "account")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;
	
	@Column(nullable = false)
	private String accountName;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountType accountType;
	
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
