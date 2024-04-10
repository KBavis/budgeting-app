package com.bavis.budgetapp.exception;

public class PlaidServiceException extends RuntimeException{
    public PlaidServiceException(String msg) { super("PlaidServiceException: [" + msg + "]"); }
}
