package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.IncomeDTO;
import com.bavis.budgetapp.model.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IncomeMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "incomeId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Income toIncome(IncomeDTO incomeDTO);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "incomeId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateIncomeFromDTO(@MappingTarget Income income, IncomeDTO incomeDTO);
}
