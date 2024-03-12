package com.bavis.budgetapp.request;

import com.bavis.budgetapp.dto.PlaidUserDTO;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class LinkTokenRequest {

    @Value(value = "${plaid.api.client-id")
    private final String clientId;

    @Value(value = "${plaid.api.secret")
    private final String secretKey;

    private String clientName;

    private String[] countryCodes;

    private String language;

    private PlaidUserDTO user;

    private String[] products;

}
