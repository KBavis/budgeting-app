package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.exception.BadAuthenticationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccountConnectionAdvice {
    @ResponseBody
    @ExceptionHandler(AccountConnectionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String badAccountConnectionHandler(AccountConnectionException ex) { return ex.getMessage(); }
}
