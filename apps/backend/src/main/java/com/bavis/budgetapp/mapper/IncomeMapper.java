package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.response.IncomeResponseDto;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.entity.IncomeVt;
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


    @Mapping(target = "incomeId", source = "income.incomeId")
    @Mapping(target = "startDate", source = "income.startDate")
    @Mapping(target = "endDate", source = "income.endDate")
    @Mapping(target = "updatedAt", source = "income.updatedAt")
    @Mapping(target = "amount", source = "activeVt.amount")
    @Mapping(target = "incomeType", source = "activeVt.incomeType")
    @Mapping(target = "incomeSource", source = "activeVt.incomeSource")
    @Mapping(target = "description", source = "activeVt.description")
    IncomeResponseDto toResponseDto(Income income, IncomeVt activeVt);
}
