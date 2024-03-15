package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.BadAuthenticationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BadAuthenticationAdvice {
    @ResponseBody
    @ExceptionHandler(BadAuthenticationRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badAuthenticationHandler(BadAuthenticationRequest ex) { return ex.getMessage(); }
}