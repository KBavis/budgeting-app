package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.entity.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Kellen Bavis
 *
 * Mapper used to map IncomeDTO to Income Entity
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IncomeMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "incomeId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Income toIncome(IncomeDto incomeDTO);
}
