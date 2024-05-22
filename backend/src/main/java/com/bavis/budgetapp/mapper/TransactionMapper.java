package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
    @Mapping(target = "date", expression = "java(getDate(dto))")
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

    /**
     * Function to fetch the Counterparty object 'logoUrl' attribute
     *
     * @param counterparties
     *          - CounterpartyDto object to fetch logoUrl for
     * @return
     *          - corresponding logo url
     */
    default String getFirstCounterpartyLogoUrl(List<PlaidTransactionDto.CounterpartyDto> counterparties) {
        if(counterparties != null && !counterparties.isEmpty()) {
           return counterparties.get(0).getLogo_url();
        }
        return null;
    }

    /**
     * Function to fetch PlaidTransactionDto's relevant Transaction Date
     *
     * @param dto
     *          - PlaidTransactionDto to fetch relevant date for
     * @return
     *          - relevant LocalDate to be set as Transaction entity date
     */
    default LocalDate getDate(PlaidTransactionDto dto) {
        if(dto.getDatetime() != null) return dto.getDatetime();

        //Convert Date to LocalDate
        Date dateObject = dto.getDate() != null ? dto.getDate() : dto.getAuthorized_date();
        if(dateObject != null) {
            return dateObject.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }


}
