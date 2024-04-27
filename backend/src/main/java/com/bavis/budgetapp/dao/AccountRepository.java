package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
