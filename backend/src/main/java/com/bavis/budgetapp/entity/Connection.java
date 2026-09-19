package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.constants.PlaidErrorCode;
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
import lombok.ToString;
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
@ToString(exclude = "accessToken")
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

	/**
	 * Plaid error code explaining why this Connection needs attention (i.e. ITEM_LOGIN_REQUIRED); null when healthy
	 */
	@Column
	private String errorCode;

	/**
	 * Plaid's message explaining why this Connection needs attention; null when healthy
	 */
	@Column(length = 500)
	private String errorMessage;

	/**
	 * Whether the User must re-authenticate this Connection via Plaid Link's update mode
	 *
	 * NOTE: ConnectionStatus.DISCONNECTED is utilized to represent "Plaid requires the User to take action"; the
	 * reason is stored in errorCode. (A new enum value would require altering the DB check constraint that Hibernate
	 * generates for the enum column, which 'ddl-auto: update' does not do.)
	 */
	public boolean requiresReauthentication() {
		return connectionStatus == ConnectionStatus.DISCONNECTED && PlaidErrorCode.ITEM_LOGIN_REQUIRED.equals(errorCode);
	}
}