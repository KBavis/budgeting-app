import {
   ACCOUNTS_FETCHED,
   ACCOUNTS_FETCH_FAILED,
   ACCOUNT_CREATED,
   ACCOUNT_FAILED_CREATED,
   ACCOUNT_FAILED_DELETED,
   CLEAR_ERRORS,
   SET_LOADING, REMOVE_ACCOUNT_SUCCESS, REMOVE_ACCOUNT_FAILURE,
   UPDATE_ACCOUNT_BALANCE,
} from "./types";

/**
 * Reducer to handle different actions for our Account management
 */
export default (state, action) => {
   switch (action.type) {
      case UPDATE_ACCOUNT_BALANCE:
         return {
            ...state,
            accounts: state.accounts ? 
               state.accounts.map(account => {
                  const updatedAccount = action.payload.find(
                     updated => updated.accountId === account.accountId
                  );

                  return updatedAccount 
                     ? { ...account, balance: updatedAccount.balance} 
                     : account
               })
            : []
         };
      case ACCOUNT_CREATED:
         return {
            ...state,
            accounts: state.accounts
               ? [...state.accounts, action.payload]
               : [action.payload],
            loading: false,
            error: null,
         };
      case REMOVE_ACCOUNT_SUCCESS:
         return {
            ...state,
            accounts: state.accounts ?
                state.accounts.filter((account) => account.accountId !== action.payload) :
                [],
            loading: false,
            error: null
         }
      case ACCOUNTS_FETCHED:
         return {
            ...state,
            accounts: action.payload,
         };
      case ACCOUNT_FAILED_CREATED:
      case REMOVE_ACCOUNT_FAILURE:
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
      case SET_LOADING:
         return {
            ...state,
            loading: true,
         };
      default:
         return state;
   }
};
