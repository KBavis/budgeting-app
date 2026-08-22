package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bavis.budgetapp.constants.TemporalConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Kellen Bavis
 * 
 * Entity To Relate an Account with a User
 */
@Entity
@Table(name = "account")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {
	@Id @JsonProperty("accountId")
	private String accountId;

	@Builder.Default
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = LocalDate.now();

	@Builder.Default
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate = TemporalConstants.END_OF_TIME;

	@ManyToOne
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;

	@ManyToOne
	@JoinColumn(name = "connectionId")
	@JsonIgnore
	private Connection connection;

	@Builder.Default
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<AccountVt> validTimes = new ArrayList<>();
}