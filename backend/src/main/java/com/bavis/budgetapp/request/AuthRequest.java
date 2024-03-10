package com.bavis.budgetapp.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class AuthRequest {
    private final String name;
    private final String username;
    private final String passwordOne;
    private final String passwordTwo; //validate same password
}
