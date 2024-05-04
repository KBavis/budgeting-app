package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.constants.AccountType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate needed information for connecting a new Account
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectAccountRequestDto {

    @NotEmpty(message = "plaidAccountId must not be empty")
    private String plaidAccountId; //plaids account ID

    @NotEmpty(message = "accountName must not be empty")
    private String accountName;

    @NotEmpty(message = "publicToken must not be empty")
    private String publicToken;

    @NotNull(message = "accountType must not be null")
    private AccountType accountType;

}
