package com.bavis.budgetapp.exception;

public class BadAuthenticationRequest extends RuntimeException{
    public BadAuthenticationRequest(String msg) {
        super("Bad Authentication Request Exception[" + msg + "]");
    }
}
