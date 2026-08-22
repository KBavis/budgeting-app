package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.constants.TemporalConstants;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * Entity Used To Establish a Connection With an Account and a Specified User
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

	@Builder.Default
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = LocalDate.now();

	@Builder.Default
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate = TemporalConstants.END_OF_TIME;

	@Column(nullable = false)
	private String accessToken;

	@Column(nullable = false)
	private String institutionName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConnectionStatus connectionStatus;

	@Column(nullable = false)
	private LocalDateTime lastSyncTime;

	@Column
	private String previousCursor;

	@Column
	private String originalCursor;
}