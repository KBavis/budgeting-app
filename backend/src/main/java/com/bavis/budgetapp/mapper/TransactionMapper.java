package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.dto.TransactionDto;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.model.PlaidConfidenceLevel;
import com.bavis.budgetapp.model.PlaidDetailedCategory;
import com.bavis.budgetapp.model.PlaidPrimaryCategory;
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

    /**
     * Function to map a PlaidTransactionDto to a Transaction entity
     *
     * @param dto
     *          - PlaidTransactionDto to convert to Transaction entity
     * @return
     *          - Transaction entity with PlaidTransactionDto properties
     */
    @Mapping(target = "name", expression = "java(getFirstCounterpartyName(dto.getCounterparties()))")
    @Mapping(target = "logoUrl", expression = "java(getFirstCounterpartyLogoUrl(dto.getCounterparties()))")
    @Mapping(target = "transactionId", source = "transaction_id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "dateTime", source = "datetime")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "merchantName", source = "java(dto.getMerchantName() != null ? dto.getMerchantName() : getFirstCounterpartyName(dto.getCounterparties()))")
    @Mapping(target = "personalFinanceCategory", expression=  "java(mapPersonalFinanceCategory(dto.getPersonal_finance_category()))")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "category", ignore = true)
    Transaction toEntity(PlaidTransactionDto dto);


    /**
     * Function to map a TransactionDto to a Transaction entity
     *
     *
     * @param transactionDto
     *          - TransactionDto to map to Transaction entity
     * @return
     *          - New Transaction entity
     */
    @Mapping(target = "name", source = "updatedName")
    @Mapping(target = "amount", source = "updatedAmount")
    @Mapping(target = "logoUrl", source = "logoUrl")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "transactionId", ignore = true)
    Transaction toEntity(TransactionDto transactionDto);


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
     * Map Personal Finance Category in DTO to Entitiy
     *
     * @param dto
     *          - dto to be mapped
     * @return
     *          - mapped personal finance category
     */
    default Transaction.PersonalFinanceCategory mapPersonalFinanceCategory(PlaidTransactionDto.PersonalFinanceCategoryDto dto) {
        if (dto == null) return  null;

        Transaction.PersonalFinanceCategory personalFinanceCategory = new Transaction.PersonalFinanceCategory();

        try {
            personalFinanceCategory.setPlaidConfidenceLevel(
                    dto.getConfidence_level() != null ? PlaidConfidenceLevel.valueOf(dto.getConfidence_level().toUpperCase()) : null
            );
        } catch (IllegalArgumentException e) {
            personalFinanceCategory.setPlaidConfidenceLevel(null);
        }

        try {
            personalFinanceCategory.setPrimaryCategory(
                    dto.getPrimary() != null ? PlaidPrimaryCategory.valueOf(dto.getPrimary().toUpperCase()) : null
            );
        } catch (IllegalArgumentException e) {
            personalFinanceCategory.setPrimaryCategory(null);
        }

        try {
            personalFinanceCategory.setDetailedCategory(
                    dto.getDetailed() != null ? PlaidDetailedCategory.valueOf(dto.getDetailed().toUpperCase()) : null
            );
        } catch (IllegalArgumentException e) {
            personalFinanceCategory.setDetailedCategory(null);
        }

        return personalFinanceCategory;
    }

}
