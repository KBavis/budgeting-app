package com.bavis.budgetapp.model;

import java.time.LocalDateTime;

import com.bavis.budgetapp.enumeration.ConnectionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
 *  	Entity Used To Establish a Connection With an Account and a Specified User
 *
 */
@Entity
@Table(name = "connection")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Connection {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long connectionId;

	//TODO: This information may need to be encrypted for security purposes within our db
	@Column(nullable = false)
	private String accessToken; //Plaid Access Token needed to access accounts
	
	@Column(nullable = false)
	private String institutionName;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConnectionStatus connectionStatus;
	
	@Column(nullable = false)
	private LocalDateTime lastSyncTime;
	
	@OneToOne
	@JoinColumn(name = "accountId", nullable = false) //one account associated to one connection
	private Account account;
}
