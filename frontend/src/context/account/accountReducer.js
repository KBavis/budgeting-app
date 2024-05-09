import {
   ACCOUNT_CREATED,
   ACCOUNT_DELETED,
   ACCOUNT_FAILED_CREATED,
   ACCOUNT_FAILED_DELETED,
   CLEAR_ERRORS,
} from "./types";

/**
 *  Reducer to handle different actions for our Account management
 */
export default (state, action) => {
   switch (action.type) {
      case ACCOUNT_CREATED:
         return {
            ...state,
            ...action.payload,
         };
      case ACCOUNT_DELETED:
         return {
            ...state,
         };
      case ACCOUNT_FAILED_CREATED:
         return {
            ...state,
         };
      case ACCOUNT_FAILED_DELETED:
         return {
            ...state,
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
