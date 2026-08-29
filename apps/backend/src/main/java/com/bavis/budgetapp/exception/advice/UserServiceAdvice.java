package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.UserServiceException;
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
 * Controller Advice for our UserServiceException
 */
@ControllerAdvice
public class UserServiceAdvice {
    @ResponseBody
    @ExceptionHandler(UserServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, String> userNotFoundHandler(UserServiceException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}
