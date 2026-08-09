package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.request.IncomeDto;
import com.bavis.budgetapp.dto.response.IncomeResponseDto;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.constants.TemporalConstants;
import com.bavis.budgetapp.entity.IncomeVt;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.util.ArrayList;

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
