package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.ConnectionCreationException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ConnectionCreationAdvice {
    @ResponseBody
    @ExceptionHandler(ConnectionCreationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String badRegistrationHandler(ConnectionCreationException ex) { return ex.getMessage(); }
}
