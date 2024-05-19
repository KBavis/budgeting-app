import {
   ACCOUNTS_FETCHED,
   ACCOUNTS_FETCH_FAILED,
   ACCOUNT_CREATED,
   ACCOUNT_DELETED,
   ACCOUNT_FAILED_CREATED,
   ACCOUNT_FAILED_DELETED,
   CLEAR_ERRORS,
} from "./types";

/**
 * Reducer to handle different actions for our Account management
 */
export default (state, action) => {
   switch (action.type) {
      case ACCOUNT_CREATED:
         return {
            ...state,
            accounts: state.accounts
               ? [...state.accounts, action.payload]
               : [action.payload],
            loading: false,
            error: null,
         };
      case ACCOUNT_DELETED:
         return {
            ...state,
            accounts: state.accounts
               ? state.accounts.filter(
                    (account) => account.id !== action.payload
                 )
               : [],
            loading: false,
            error: null,
         };
      case ACCOUNTS_FETCHED:
         return {
            ...state,
            accounts: action.payload,
         };
      case ACCOUNT_FAILED_CREATED:
         return {
            ...state,
            loading: false,
            error: action.payload,
         };
      case ACCOUNTS_FETCH_FAILED:
      case ACCOUNT_FAILED_DELETED:
         return {
            ...state,
            loading: false,
            error: action.payload,
         };
      case CLEAR_ERRORS:
         return {
            ...state,
            error: null,
         };
      default:
         return state;
   }
};
