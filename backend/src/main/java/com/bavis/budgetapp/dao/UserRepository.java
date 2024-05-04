package com.bavis.budgetapp.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bavis.budgetapp.entity.User;

import java.util.Optional;

/**
 * @author Kellen Bavis
 *
 *  DAO for working with User entities
 */
public interface UserRepository extends JpaRepository<User, Long>{
    /**
     * Fetch User by Username
     *
     * @param username
     *          - username to search for
     * @return
     *          - User entity pertaining to specific username
     */
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
