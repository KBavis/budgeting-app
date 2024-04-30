package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.BulkCategoryDtoValidList;
import com.bavis.budgetapp.validator.group.CategoryDtoValidationGroup;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@BulkCategoryDtoValidList
@Builder
public class BulkCategoryDto {
    @Valid
    private List<CategoryDto> categories;
}
