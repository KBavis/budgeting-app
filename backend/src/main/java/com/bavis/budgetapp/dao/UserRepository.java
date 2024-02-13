package com.bavis.budgetapp.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bavis.budgetapp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{}
