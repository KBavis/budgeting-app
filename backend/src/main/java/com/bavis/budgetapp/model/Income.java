package com.bavis.budgetapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 *  	Entity To Establish The Relationship Between Amount Of Income for a User
 *
 */
@Entity
@Table(name = "income")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Income {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long incomeId;
	
	@Column(nullable = false)
	private double amount;
	
	@OneToOne
	@JoinColumn(name = "userId", nullable = false, unique = true)
	private User user;

}
