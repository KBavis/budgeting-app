package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.response.CategoryResponseDto;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryVt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Kellen Bavis
 *
 * Mapper for converting Category entity + active CategoryVt snapshot to Response DTO
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "startDate", source = "category.startDate")
    @Mapping(target = "endDate", source = "category.endDate")
    @Mapping(target = "name", source = "activeVt.name")
    @Mapping(target = "budgetAllocationPercentage", source = "activeVt.budgetAllocationPercentage")
    @Mapping(target = "budgetAmount", source = "activeVt.budgetAmount")
    @Mapping(target = "categoryTypeId", source = "activeVt.categoryType.categoryTypeId")
    CategoryResponseDto toResponseDto(Category category, CategoryVt activeVt);
}
