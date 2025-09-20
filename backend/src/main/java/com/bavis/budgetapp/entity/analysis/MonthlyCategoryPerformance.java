package com.bavis.budgetapp.entity.analysis;

import com.bavis.budgetapp.model.MonthYear;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

/**
 *
 * Entity to store more granular analysis on users monthly spending
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyCategoryPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryPerformanceId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "month", column = @Column(name = "performance_month")),
            @AttributeOverride(name = "year", column = @Column(name = "performance_year"))
    })
    private MonthYear monthYear;

    @Column(name = "category_type_id")
    private Long categoryTypeId;

    @Column(name = "total_spend")
    private Double totalSpend;

    @Column(name = "total_amount_allocated")
    private Double totalAmountAllocated;

    @Column(name = "category_utilization")
    private Double categoryPercentUtilization;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "top_merchants", columnDefinition = "jsonb")
    private List<MerchantAnalysis> topMerchants;

}
