package com.bavis.budgetapp.entity;

import java.io.Serial;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.bavis.budgetapp.constants.Role;
import com.bavis.budgetapp.constants.TemporalConstants;
import com.bavis.budgetapp.model.LinkToken;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @author Kellen Bavis
 * 	
 * User Entity For Allowing Users To Authenticate and Generate Account Specific Configurations
 *
 */
@Entity
@Table(name = "budgetUser")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails {
	@Serial
	private static final long serialVersionUID = 1L;

	@Id @JsonProperty("userId") @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Builder.Default
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate = LocalDate.now();

	@Builder.Default
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate = TemporalConstants.END_OF_TIME;

	private String name;
	private String username;
	private String password;
	private String profileImage;
	private int failedLoginAttempts;
	private LocalDateTime lockoutEndTime;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "token", column = @Column(name = "link_token")),
			@AttributeOverride(name = "expiration", column = @Column(name = "link_token_expiration"))
	})
	private LinkToken linkToken;

	@Enumerated(EnumType.STRING)
	private Role role;
	
	/**
	 * One User Can Have Many Categories 
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonIgnore
	List<Category> categories;
	
	/**
	 * User Can Have Multiple Accounts
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Account> accounts;

	/**
	 * User Can Have Multiple Sources of Income
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Income> incomes;

	public void incrementFailedLoginAttempts() {
		failedLoginAttempts++;
	}

	public void lockAccount(int minutes) {
		lockoutEndTime = LocalDateTime.now().plus(Duration.ofMinutes(minutes));
	}

	public void resetLoginAttempts() {
		failedLoginAttempts = 0;
		lockoutEndTime = null;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	@JsonProperty("userName")
	public String getUsername() {
		return username;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return lockoutEndTime == null || lockoutEndTime.isAfter(LocalDateTime.now());
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}

}
