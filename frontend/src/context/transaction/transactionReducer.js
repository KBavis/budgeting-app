import {
   SYNC_TRANSACTIONS_SUCCESS,
   SYNC_TRANSACTIONS_FAIL,
   CLEAR_ERRORS,
} from "./types";

export default (state, action) => {
   switch (action.type) {
      case SYNC_TRANSACTIONS_SUCCESS:
         //New & Updated Transactions
         const newTransactions = action.payload.reduce(
            (acc, transaction) => {
               if (state.transactions) {
                  const existingTransactionIndex = state.transactions.findIndex(
                     (t) => t.transactionId === transaction.transactionId
                  );
                  if (existingTransactionIndex !== -1) {
                     acc[existingTransactionIndex] = transaction;
                  } else {
                     acc.push(transaction);
                  }
               } else {
                  acc.push(transaction);
               }
               return acc;
            },
            state.transactions ? [...state.transactions] : []
         );
         return {
            ...state,
            transactions: newTransactions,
            loading: false,
            error: null,
         };
      case SYNC_TRANSACTIONS_FAIL:
         return {
            ...state,
            error: action.payload,
            loading: false,
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
