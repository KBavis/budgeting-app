package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {
    @Mapping(target = "transactionId", source = "transaction_id")
    @Mapping(target = "name", source = "counterparties.name")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "date", source = "datetime")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "category", ignore = true)
    Transaction toEntity(PlaidTransactionDto dto);
}
