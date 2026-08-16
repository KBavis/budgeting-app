package com.bavis.budgetapp.dto.request;

import com.bavis.budgetapp.annotation.AddCategoryDtoValidAttributes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kellen Bavis
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@AddCategoryDtoValidAttributes
public class AddCategoryDto {
    private List<UpdateCategoryDto> updatedCategories;
    private CategoryDto addedCategory;
}
