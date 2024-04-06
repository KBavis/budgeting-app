package com.bavis.budgetapp.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class AuthRequest {
    private final String name;
    private final String username;
    private final String passwordOne;
    private final String passwordTwo; //validate same password
}
