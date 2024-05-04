package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "accountName", source = "accountName")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "accountType", source = "accountType")
    AccountDto toDTO(Account account);
}