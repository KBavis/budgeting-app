package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.response.CategoryTypeResponseDto;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryTypeVt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Kellen Bavis
 *
 * Mapper for converting CategoryType entity + active CategoryTypeVt snapshot to Response DTO
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryTypeMapper {

    @Mapping(target = "categoryTypeId", source = "categoryType.categoryTypeId")
    @Mapping(target = "startDate", source = "categoryType.startDate")
    @Mapping(target = "endDate", source = "categoryType.endDate")
    @Mapping(target = "name", source = "activeVt.name")
    @Mapping(target = "budgetAllocationPercentage", source = "activeVt.budgetAllocationPercentage")
    @Mapping(target = "budgetAmount", source = "activeVt.budgetAmount")
    @Mapping(target = "savedAmount", source = "activeVt.savedAmount")
    CategoryTypeResponseDto toResponseDto(CategoryType categoryType, CategoryTypeVt activeVt);
}
