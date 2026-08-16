package com.bavis.budgetapp.service;

import com.bavis.budgetapp.constants.TemporalConstants;
import com.bavis.budgetapp.entity.ValidTimeEntity;
import com.bavis.budgetapp.util.GeneralUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Centralized service managing valid-time (VT) effectivity state, point-in-time retrieval,
 * same-day mutation, and temporal sequence continuity across all VT entities.
 */
@Service
public class EffectivityService {

    /**
     * Retrieves the active VT record as of a specified point-in-time date.
     *
     * @param validTimes
     *          - List of VT records
     * @param asOf
     *          - Target date to query active record
     * @return
     *          - Active VT record matching date, or null if list is empty
     */
    public <V extends ValidTimeEntity> V getActiveVt(List<V> validTimes, LocalDate asOf) {
        if (validTimes == null || validTimes.isEmpty()) {
            throw new IllegalStateException("Corrupt data state: Entity contains no valid time (VT) records.");
        }
        LocalDate target = (asOf != null) ? asOf : LocalDate.now();
        return validTimes.stream()
                .filter(vt -> GeneralUtil.isActive(vt.getStartDate(), vt.getEndDate(), target))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Corrupt data state: No active valid time (VT) record found as of " + target));
    }

    /**
     * Applies a candidate VT record update into the given validTimes list.
     * Handles initial creation, same-day attribute mutations, and new-day temporal sequence additions.
     *
     * @param validTimes
     *          - Parent list of VT records to manage
     * @param newVt
     *          - Candidate VT record to append or copy attributes from
     * @param effectiveDate
     *          - Date on which the VT takes effect
     * @return
     *          - Active or updated VT record
     */
    public <V extends ValidTimeEntity> V applyVtUpdate(List<V> validTimes, V newVt, LocalDate effectiveDate) {
        LocalDate targetDate = (effectiveDate != null) ? effectiveDate : LocalDate.now();

        // 1. Initial Creation (First VT setup)
        if (validTimes == null || validTimes.isEmpty()) {
            newVt.setStartDate(effectiveDate != null ? effectiveDate : TemporalConstants.BEGINNING_OF_TIME);
            newVt.setEndDate(TemporalConstants.END_OF_TIME);
            if (validTimes != null) {
                validTimes.add(newVt);
            }
            return newVt;
        }

        V active = getActiveVt(validTimes, targetDate);

        // 2. Same-Day Update: Start date matches target date -> Mutate existing active VT directly
        if (active != null && active.getStartDate().equals(targetDate)) {
            active.copyAttributesFrom(newVt);
            return active;
        }

        // 3. New-Day Update: End-date active VT and append new VT record
        if (active != null) {
            active.setEndDate(targetDate.minusDays(1));
        }

        newVt.setStartDate(targetDate);
        newVt.setEndDate(TemporalConstants.END_OF_TIME);
        validTimes.add(newVt);

        return newVt;
    }

    /**
     * Validates timeline continuity from BOT to EOT with zero gaps or overlaps.
     *
     * @param validTimes
     *          - List of VT records to validate
     */
    public <V extends ValidTimeEntity> void validateVtSequence(List<V> validTimes) {
        if (validTimes == null || validTimes.isEmpty()) {
            throw new IllegalStateException("Entity contains no VT records");
        }

        List<V> sorted = validTimes.stream()
                .sorted(Comparator.comparing(ValidTimeEntity::getStartDate))
                .toList();

        if (!sorted.get(0).getStartDate().equals(TemporalConstants.BEGINNING_OF_TIME)) {
            throw new IllegalStateException("VT timeline must start at BEGINNING_OF_TIME");
        }

        if (!sorted.get(sorted.size() - 1).getEndDate().equals(TemporalConstants.END_OF_TIME)) {
            throw new IllegalStateException("VT timeline must end at END_OF_TIME");
        }

        for (int i = 0; i < sorted.size() - 1; i++) {
            V current = sorted.get(i);
            V next = sorted.get(i + 1);

            if (!current.getEndDate().plusDays(1).equals(next.getStartDate())) {
                throw new IllegalStateException(String.format(
                    "Gap or overlap detected between VT records: [%s to %s] and [%s to %s]",
                    current.getStartDate(), current.getEndDate(), next.getStartDate(), next.getEndDate()
                ));
            }
        }
    }
}
