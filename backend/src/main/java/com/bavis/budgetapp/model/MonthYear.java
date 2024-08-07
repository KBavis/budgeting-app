package com.bavis.budgetapp.model;

import com.bavis.budgetapp.annotation.MonthYearValidMonthAndYear;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@EqualsAndHashCode
@MonthYearValidMonthAndYear
public class MonthYear implements Serializable {
    private String month;
    private int year;
}
