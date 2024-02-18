package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
