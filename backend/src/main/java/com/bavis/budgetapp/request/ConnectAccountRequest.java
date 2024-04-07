package com.bavis.budgetapp.request;

import com.bavis.budgetapp.enumeration.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectAccountRequest {

    private String plaidAccountId; //plaids account ID

    private String accountName;

    private String publicToken;

    private AccountType accountType;

}
