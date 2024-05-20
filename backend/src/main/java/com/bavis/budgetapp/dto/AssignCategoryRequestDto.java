package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.AssignCategoryRequestValidUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@AssignCategoryRequestValidUser
public class AssignCategoryRequestDto {
    private String transactionId;
    private String categoryId;
}
