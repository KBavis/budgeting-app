package com.bavis.budgetapp.dao;

import com.bavis.budgetapp.entity.VenmoAutomation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Kellen Bavis
 *
 * DAO for working with VenmoAutomation entities
 */
public interface VenmoAutomationRepository extends JpaRepository<VenmoAutomation, Long> {

    /**
     * Find a VenmoAutomation by its unique ingest token.
     * Used to resolve which user an inbound Venmo email belongs to.
     *
     * @param ingestToken
     *          - the UUID token extracted from the recipient email address
     * @return
     *          - the VenmoAutomation record if found
     */
    Optional<VenmoAutomation> findByIngestToken(String ingestToken);

    /**
     * Find a VenmoAutomation by user ID.
     *
     * @param userId
     *          - the user's ID
     * @return
     *          - the VenmoAutomation record for the user if one exists
     */
    Optional<VenmoAutomation> findByUserUserId(Long userId);

    /**
     * Check if a VenmoAutomation already exists for a given user.
     *
     * @param userId
     *          - the user's ID
     * @return
     *          - true if automation is already configured
     */
    boolean existsByUserUserId(Long userId);
}
