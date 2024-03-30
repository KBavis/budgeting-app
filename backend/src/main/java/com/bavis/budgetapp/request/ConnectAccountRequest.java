package com.bavis.budgetapp.request;

import com.bavis.budgetapp.enumeration.AccountType;
import lombok.Data;


@Data
public class ConnectAccountRequest {

    private String plaidAccountId; //plaids account ID

    private String accountName;

    private String publicToken;

    private AccountType accountType;

}
