package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;


/**
 * @author Kellen Bavis
 *
 * Mapper for converting a Category entity to DTO
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "budgetAllocationPercentage", source = "budgetAllocationPercentage")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "budgetAmount", source = "budgetAmount")
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "categoryType", ignore = true)
    @Mapping(target = "user", ignore = true)
    Category toEntity(CategoryDto account);
}
