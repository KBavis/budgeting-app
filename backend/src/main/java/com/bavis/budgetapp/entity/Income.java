package com.bavis.budgetapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bavis.budgetapp.constants.TemporalConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Kellen Bavis
 *
 * Entity for storing information regarding a user's monthly income
 */
@Entity
@Table(name = "income")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeId;

    @Builder.Default
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = TemporalConstants.BEGINNING_OF_TIME;

    @Builder.Default
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate = TemporalConstants.END_OF_TIME;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private User user;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "income", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<IncomeVt> validTimes = new ArrayList<>();
}
