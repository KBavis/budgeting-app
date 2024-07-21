package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.RenameCategoryDtoValidName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to encapsulate information needed to update a Category name
 *
 * @author Kellen Bavis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RenameCategoryDtoValidName
public class RenameCategoryDto {
    private String categoryName;
    private long categoryId;
}
