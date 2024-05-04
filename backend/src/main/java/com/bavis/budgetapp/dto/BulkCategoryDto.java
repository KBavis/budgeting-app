package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.annotation.BulkCategoryDtoValidList;
import com.bavis.budgetapp.validator.group.BulkCategoryDtoValidationGroup;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate a list of CategoryDtos for bulk Category creation requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@BulkCategoryDtoValidList(groups = BulkCategoryDtoValidationGroup.class)
@Builder
public class BulkCategoryDto {
    @Valid
    private List<CategoryDto> categories;
}
