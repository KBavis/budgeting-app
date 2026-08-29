package com.bavis.budgetapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bavis.budgetapp.entity.Connection;

/**
 * @author Kellen Bavis
 *
 *  DAO for working with Connection entities
 */
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
