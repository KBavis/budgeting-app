package com.bavis.budgetapp.exception;

/**
 * @author Kellen Bavis
 *
 * Exception for any issues that occur while working with User entities
 */
public class UserServiceException extends RuntimeException{
    public UserServiceException(String msg) {
        super(msg);
    }
}
