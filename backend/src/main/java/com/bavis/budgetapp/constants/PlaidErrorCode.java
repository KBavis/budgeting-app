package com.bavis.budgetapp.constants;

/**
 * @author Kellen Bavis
 *
 * Plaid API error codes that our application reacts to
 *
 * @see <a href="https://plaid.com/docs/errors/">Plaid Errors</a>
 */
public final class PlaidErrorCode {

    private PlaidErrorCode() {}

    /**
     * The user's login details at their financial institution changed (or the institution requires them to act);
     * the User must re-authenticate the Item via Plaid Link's update mode
     */
    public static final String ITEM_LOGIN_REQUIRED = "ITEM_LOGIN_REQUIRED";
}
