package com.bavis.budgetapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddCategoryDto {
    private List<UpdateCategoryDto> updatedCategories;
    private CategoryDto addedCategory;
}
