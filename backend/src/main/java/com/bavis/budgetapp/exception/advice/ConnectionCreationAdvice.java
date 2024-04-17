package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.ConnectionCreationException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ConnectionCreationAdvice {
    @ResponseBody
    @ExceptionHandler(ConnectionCreationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String,String> connectCreationExceptionHandler(ConnectionCreationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}
