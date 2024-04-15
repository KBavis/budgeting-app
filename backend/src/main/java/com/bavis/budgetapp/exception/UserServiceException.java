package com.bavis.budgetapp.exception;

public class UserServiceException extends RuntimeException{
    public UserServiceException(String msg) {
        super("UserServiceException: [" + msg + "]");
    }

    //Could not find user with the ID [" + id + "]"
    //"Could not find user with the username [" + username + "]"
}
