package com.bavis.budgetapp.request;

import com.bavis.budgetapp.constants.AccountType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectAccountRequest {

    @NotEmpty
    private String plaidAccountId; //plaids account ID

    @NotEmpty
    private String accountName;

    @NotEmpty
    private String publicToken;

    @NotNull
    private AccountType accountType;

}
