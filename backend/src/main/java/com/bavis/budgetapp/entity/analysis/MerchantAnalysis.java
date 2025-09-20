package com.bavis.budgetapp.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MerchantAnalysis {
    private String merchantName;
    private Integer merchantRank;
    private Long totalSpent;
    private Integer transactionCount;
    private Double avgTransactionAmount;
}
