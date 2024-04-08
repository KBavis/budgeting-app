package com.bavis.budgetapp.exception;

public class ConnectionCreationException extends RuntimeException{
   public ConnectionCreationException(String msg) {
       super("Error occurred when creating a connection {" + msg +"}");
   }
}
