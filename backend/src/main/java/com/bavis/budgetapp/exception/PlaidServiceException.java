package com.bavis.budgetapp.exception;

/**
 * @author Kellen Bavis
 *
 * Exception thrown when an interaction with the Plaid API fails
 */
public class PlaidServiceException extends RuntimeException{

    /**
     * Plaid's error_code (i.e. ITEM_LOGIN_REQUIRED), if Plaid provided one
     */
    private final String errorCode;

    /**
     * Plaid's original error message (without our exception prefix), if available
     */
    private final String plaidMessage;

    public PlaidServiceException(String msg) {
        this(null, msg);
    }

    public PlaidServiceException(String errorCode, String msg) {
        super("PlaidServiceException: [" + msg + "]");
        this.errorCode = errorCode;
        this.plaidMessage = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getPlaidMessage() {
        return plaidMessage;
    }
}
