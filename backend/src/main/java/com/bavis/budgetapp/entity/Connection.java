package com.bavis.budgetapp.entity;

import java.time.LocalDateTime;

import com.bavis.budgetapp.constants.ConnectionStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Kellen Bavis
 * 
 *  Entity Used To Establish a Connection With an Account and a Specified User
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
	@JsonProperty("connection_id")
	private Long connectionId;

	@Column(nullable = false)
	private String accessToken;

	@Column(nullable = false)
	private String institutionName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConnectionStatus connectionStatus;

	@Column(nullable = false)
	private LocalDateTime lastSyncTime;
}