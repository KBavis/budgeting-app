package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.CategoryTypeDto;
import com.bavis.budgetapp.entity.CategoryType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper used to Map CategoryTypeDto to CategoryType Entity
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryTypeMapper {

    @Mapping(target = "budgetAllocationPercentage", source = "budgetAllocationPercentage")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "categoryTypeId", ignore = true)
    @Mapping(target = "budgetAmount", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "user", ignore = true)
    CategoryType toEntity(CategoryTypeDto account);
}
