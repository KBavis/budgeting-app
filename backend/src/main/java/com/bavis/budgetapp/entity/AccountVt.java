package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.constants.AccountType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Kellen Bavis
 * 
 * Valid Time (VT) Entity for storing Account state history (name, type, balance) over time
 */
@Entity
@Table(name = "account_vt")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVt extends ValidTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private Account account;

    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private double balance;

    @Override
    public void copyAttributesFrom(ValidTimeEntity source) {
        if (source instanceof AccountVt other) {
            if (other.getAccountName() != null) this.accountName = other.getAccountName();
            if (other.getAccountType() != null) this.accountType = other.getAccountType();
            this.balance = other.getBalance();
        }
    }
}
