package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.JwtServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kellen Bavis
 *
 * Controller Advice for JwtServiceException
 */
@ControllerAdvice
public class JwtServiceAdvice {
    @ResponseBody
    @ExceptionHandler(JwtServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String,String> handleJwtServiceException(JwtServiceException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return errors;
    }
}