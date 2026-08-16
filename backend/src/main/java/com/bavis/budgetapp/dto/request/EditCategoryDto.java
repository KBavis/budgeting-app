package com.bavis.budgetapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kellen Bavis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditCategoryDto {
    private List<UpdateCategoryDto> updatedCategories;
    private long categoryTypeId;
}
