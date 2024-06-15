package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidAllocatedAmount;
import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidPercentAllocated;
import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidSavedAmount;
import com.bavis.budgetapp.validator.group.UpdateCategoryTypeDtoValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@UpdateCategoryTypeValidPercentAllocated(groups = UpdateCategoryTypeDtoValidationGroup.class)
@UpdateCategoryTypeValidSavedAmount(groups = UpdateCategoryTypeDtoValidationGroup.class)
@UpdateCategoryTypeValidAllocatedAmount(groups = UpdateCategoryTypeDtoValidationGroup.class)
public class UpdateCategoryTypeDto {
    private double budgetAllocationPercentage;
    private double savedAmount;
    private double amountAllocated;
}
