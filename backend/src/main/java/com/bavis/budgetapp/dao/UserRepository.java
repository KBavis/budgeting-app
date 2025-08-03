package com.bavis.budgetapp.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bavis.budgetapp.entity.User;

import javax.swing.text.html.Option;
import java.util.List;
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

    @Query(value = "SELECT DISTINCT b.user_id FROM budget_user b JOIN account a ON a.user_id = b.user_id WHERE account_id IN :accountIds", nativeQuery = true)
    List<Long> findUserIdByAccountIds(@Param("accountIds") List<String> accountIds);
}
