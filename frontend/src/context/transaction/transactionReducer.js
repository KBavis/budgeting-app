import {
   SYNC_TRANSACTIONS_SUCCESS,
   SYNC_TRANSACTIONS_FAIL,
   UPDATE_TRANSACTION_CATEGORY,
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
      case UPDATE_TRANSACTION_CATEGORY:
         //Update Transactions Category ID
         const { transactionId, categoryId } = action.payload;
         const updatedTransactions = state.transactions.map((transaction) => {
            if (transaction.transactionId === transactionId) {
               return {
                  ...transaction,
                  category: { categoryId },
               };
            }
            return transaction;
         });
         return {
            ...state,
            transactions: updatedTransactions,
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
