package com.bavis.budgetapp.model;

import java.io.Serial;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


import com.bavis.budgetapp.enumeration.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @author bavis
 * 	
 * 		User Entity For Allowing Users To Authenticate and Generate Account Specific Configurations 
 *
 */
@Entity
@Table(name = "budgetUser")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails{
	@Serial
	private static final long serialVersionUID = 1L;

	@Id @JsonProperty("userId") @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	private String name;
	private String username;
	private String password;
	private String profileImage;
	private String linkToken; //Plaid Link token
	private int failedLoginAttempts; //TODO: increment this value when user fails to login (if username is associated with account) AND reset once succesful login
	private LocalDateTime lockoutEndTime; //TODO: Compare users attempt to login with this lockout time
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	/**
	 * One User Can Have Many Categories 
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<Category> categories;
	
	/**
	 * User Can Have Multiple Accounts
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Account> accounts; //connected user accounts

	public void incrementFailedLoginAttempts() {
		failedLoginAttempts++;
	}

	/**
	 *
	 * @param minutes
	 * 			number of minutes to lock account for
	 */
	public void lockAccount(int minutes) {
		lockoutEndTime = LocalDateTime.now().plus(Duration.ofMinutes(minutes));
	}

	/**
	 * invoked upon succesfull authentication
	 */
	public void resetLoginAttempts() {
		failedLoginAttempts = 0;
		lockoutEndTime = null;
	}



	/**
	 * ***************************************************
	 * ****** OVERRIDING SPRING SECURITY METHODS *********
	 * ***************************************************
	 */

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

	/**
	 * TODO: Maybe setup some logic regarding users needing to reset password after X amount of time from initial creation of account
	 *
	 * @return
	 * 		whether the users credentials must be updated
	 */
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

	@Override
	public String toString() {
		return "User{" +
				"userId=" + userId +
				", name='" + name + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", profileImage='" + profileImage + '\'' +
				", linkToken='" + linkToken + '\'' +
				", failedLoginAttempts=" + failedLoginAttempts +
				", lockoutEndTime=" + lockoutEndTime +
				", role=" + role +
				", categories=" + categories +
				", accounts=" + accounts +
				'}';
	}
}
