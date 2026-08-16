package com.bavis.budgetapp.dto.request;

import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidAllocatedAmount;
import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidPercentAllocated;
import com.bavis.budgetapp.annotation.UpdateCategoryTypeValidSavedAmount;
import com.bavis.budgetapp.validator.group.UpdateCategoryTypeDtoValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate updates for CategoryType entities
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@UpdateCategoryTypeValidSavedAmount(groups = {UpdateCategoryTypeDtoValidationGroup.class})
@UpdateCategoryTypeValidAllocatedAmount(groups = {UpdateCategoryTypeDtoValidationGroup.class})
@UpdateCategoryTypeValidPercentAllocated(groups = {UpdateCategoryTypeDtoValidationGroup.class})
public class UpdateCategoryTypeDto {

    private Double savedAmount;

    private Double budgetAllocationPercentage;

    private Double amountAllocated;
}
