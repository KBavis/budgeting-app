package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {
    @Mapping(target = "accountName", source = "accountName")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "accountType", source = "accountType")
    void updateAccountDTO(@MappingTarget AccountDTO target, Account source);

    @Mapping(target = "accountName", source = "accountName")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "accountType", source = "accountType")
    AccountDTO toDTO(Account account);
}