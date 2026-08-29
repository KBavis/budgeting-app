package com.bavis.budgetapp.exception;


/**
 * @author Kellen Bavis
 *
 * Exception for any issues that occur while connecting an account
 */
public class AccountConnectionException extends RuntimeException{
    public AccountConnectionException(String msg) {
        super("An error occurred when creating an account: [" + msg + "]");
    }
}
