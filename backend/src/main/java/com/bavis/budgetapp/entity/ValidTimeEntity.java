package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.constants.TemporalConstants;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * @author Kellen Bavis
 *
 * Mapped superclass for all Valid Time (VT) entities.
 * Enforces standardized startDate and endDate fields across temporal history records.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class ValidTimeEntity {

    @Builder.Default
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = TemporalConstants.BEGINNING_OF_TIME;

    @Builder.Default
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate = TemporalConstants.END_OF_TIME;

    /**
     * Polymorphic method implemented by concrete Valid Time entities to copy non-temporal domain attributes.
     * Invoked during same-day effectivity updates to mutate active record in place.
     *
     * @param source
     *          - Candidate VT record containing updated attributes
     */
    public abstract void copyAttributesFrom(ValidTimeEntity source);
}
