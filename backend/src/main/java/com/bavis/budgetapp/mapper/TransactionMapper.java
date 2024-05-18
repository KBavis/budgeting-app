package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper to map a PlaidTransaction to a Transaction entity
 *
 * @author Kellen Bavis
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "name", expression=  "java(getFirstCounterpartyName(dto.getCounterparties()))")
    @Mapping(target = "logoUrl", expression = "java(getFirstCounterpartyLogoUrl(dto.getCounterparties()))")
    @Mapping(target = "transactionId", source = "transaction_id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "date", source = "datetime")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "category", ignore = true)
    Transaction toEntity(PlaidTransactionDto dto);

    /**
     * Function to fetch the Counterparty object 'name' attribute
     *
     * @param counterparties
     *          - CounterpartDto object to fetch name attribute for
     * @return
     *          - corresponding 'name' attribute
     */
    default String getFirstCounterpartyName(List<PlaidTransactionDto.CounterpartyDto> counterparties){
        if (counterparties != null && !counterparties.isEmpty()) {
            return counterparties.get(0).getName();
        }
        return null;
    }

    default String getFirstCounterpartyLogoUrl(List<PlaidTransactionDto.CounterpartyDto> counterparties) {
        if(counterparties != null && !counterparties.isEmpty()) {
           return counterparties.get(0).getLogo_url();
        }
        return null;
    }


}
