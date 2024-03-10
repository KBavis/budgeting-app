package com.bavis.budgetapp.exception;

public class UsernameTakenException extends RuntimeException{

    public UsernameTakenException(String username) {
        super("The username [" + username + "] has already been taken.");
    }
}
