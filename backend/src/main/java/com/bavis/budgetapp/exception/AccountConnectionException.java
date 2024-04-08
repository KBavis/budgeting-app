package com.bavis.budgetapp.exception;


public class AccountConnectionException extends RuntimeException{
    public AccountConnectionException(String msg) {
        super("An error occurred when creating an account: [" + msg + "]");
    }
}
