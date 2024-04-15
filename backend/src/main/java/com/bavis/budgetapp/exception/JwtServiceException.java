package com.bavis.budgetapp.exception;

public class JwtServiceException extends RuntimeException{
    public JwtServiceException(String msg) { super("JwtServiceException: [" + msg + "]"); }
}
