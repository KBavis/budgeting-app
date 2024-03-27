package com.bavis.budgetapp.exception;

public class BadRegistrationRequestException extends RuntimeException{
    public BadRegistrationRequestException(String msg){
        super(msg);
    }
}
