import {
   ACCOUNT_CREATED,
   ACCOUNT_DELETED,
   ACCOUNT_FAILED_CRATED,
   ACCOUNT_FAILED_CRATED,
   ACCOUNT_FAILED_DELETED,
} from "./types";

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
      case ACCOUNT_FAILED_CRATED:
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
