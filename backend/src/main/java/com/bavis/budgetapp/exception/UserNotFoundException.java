package com.bavis.budgetapp.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long id) {
        super("Could not find user with the ID [" + id + "]");
    }

    public UserNotFoundException(String username) {
        super("Could not find user with the username [" + username + "]");
    }

}
