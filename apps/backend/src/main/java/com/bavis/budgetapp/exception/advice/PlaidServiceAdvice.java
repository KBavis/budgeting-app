package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.PlaidServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 *
 * Controller Advice for our PlaidServiceException
 */
@ControllerAdvice
public class PlaidServiceAdvice {
    @ResponseBody
    @ExceptionHandler(PlaidServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String,String> handlePlaidServiceException(PlaidServiceException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}