package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UsernameTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PlaidServiceAdvice {
    @ResponseBody
    @ExceptionHandler(PlaidServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRegistrationHandler(PlaidServiceException ex) { return ex.getMessage(); }
}