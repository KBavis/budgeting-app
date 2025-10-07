package com.bavis.budgetapp.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MerchantAnalysis {
    private String merchantName;
    private Integer merchantRank;
    private Double totalSpent;
    private Integer transactionCount;
    private Double avgTransactionAmount;
    private String merchantLogoUrl;
}
