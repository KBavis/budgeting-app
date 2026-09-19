package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.AccountVt;
import com.bavis.budgetapp.entity.Connection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Kellen Bavis
 *
 * Mapper for converting an Account entity to DTO
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "accountId", source = "account.accountId")
    @Mapping(target = "startDate", source = "account.startDate")
    @Mapping(target = "endDate", source = "account.endDate")
    @Mapping(target = "accountName", source = "activeVt.accountName")
    @Mapping(target = "accountType", source = "activeVt.accountType")
    @Mapping(target = "balance", source = "activeVt.balance")
    @Mapping(target = "connectionStatus", source = "account.connection.connectionStatus")
    @Mapping(target = "connectionErrorCode", source = "account.connection.errorCode")
    @Mapping(target = "requiresReauth", source = "account.connection", qualifiedByName = "requiresReauth")
    AccountResponseDto toResponseDto(Account account, AccountVt activeVt);

    @Named("requiresReauth")
    default boolean requiresReauth(Connection connection) {
        return connection != null && connection.requiresReauthentication();
    }
}