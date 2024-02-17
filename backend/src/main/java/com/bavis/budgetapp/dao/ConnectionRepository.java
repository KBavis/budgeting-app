package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.model.Connection;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

}
