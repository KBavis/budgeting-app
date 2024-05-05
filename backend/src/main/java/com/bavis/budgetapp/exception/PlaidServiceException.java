package com.bavis.budgetapp.exception;

/**
 * @author Kellen Bavis
 *
 * Exception for any issues that occur while interacting with PlaidAPI
 */
public class PlaidServiceException extends RuntimeException{
    public PlaidServiceException(String msg) { super("PlaidServiceException: [" + msg + "]"); }
}
