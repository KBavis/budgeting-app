package com.bavis.budgetapp.exception.advice;

import com.bavis.budgetapp.exception.AccountConnectionException;
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
 * Controller Advice for Our AccountConnectionException
 */
@ControllerAdvice
public class AccountConnectionAdvice {
    @ResponseBody
    @ExceptionHandler(AccountConnectionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> badAccountConnectionException(AccountConnectionException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}
