package com.bavis.budgetapp.exception;

/**
 * @author Kellen Bavis
 *
 * Exception for any issues that occur while working with JWT Tokens
 */
public class JwtServiceException extends RuntimeException{
    public JwtServiceException(String msg) { super("JwtServiceException: [" + msg + "]"); }
}
