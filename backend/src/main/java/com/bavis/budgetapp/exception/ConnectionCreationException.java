package com.bavis.budgetapp.exception;

/**
 * @author Kellen Bavis
 *
 * Exception for any issues that occur while creating a new Connection
 */
public class ConnectionCreationException extends RuntimeException{
   public ConnectionCreationException(String msg) {
       super("Error occurred when creating a connection {" + msg +"}");
   }
}
