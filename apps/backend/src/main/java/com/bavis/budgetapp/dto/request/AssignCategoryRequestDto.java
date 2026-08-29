package com.bavis.budgetapp.dto.request;

import com.bavis.budgetapp.annotation.AssignCategoryRequestValidUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@AssignCategoryRequestValidUser
public class AssignCategoryRequestDto {
    private String transactionId;
    private String categoryId;
}
