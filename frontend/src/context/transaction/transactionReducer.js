import {
   SYNC_TRANSACTIONS_SUCCESS,
   SYNC_TRANSACTIONS_FAIL,
   UPDATE_TRANSACTION_CATEGORY,
   CLEAR_ERRORS,
   REMOVE_TRANSACTION_CATEGORY,
   SET_LOADING,
   FETCH_TRANSACTIONS_FAIL,
   FETCH_TRANSACTIONS_SUCCESS,
} from "./types";

export default (state, action) => {
   switch (action.type) {
      case SYNC_TRANSACTIONS_SUCCESS:
         // New & Updated Transactions
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
         // Update Transactions Category ID
         const { transactionId, category } = action.payload;
         const updatedTransactions = state.transactions.map((transaction) => {
            if (transaction.transactionId === transactionId) {
               return {
                  ...transaction,
                  category: {
                     ...category,
                  },
               };
            }
            return transaction;
         });
         return {
            ...state,
            transactions: updatedTransactions,
         };
      case SYNC_TRANSACTIONS_FAIL:
      case FETCH_TRANSACTIONS_FAIL:
         return {
            ...state,
            error: action.payload,
            loading: false,
         };
      case FETCH_TRANSACTIONS_SUCCESS:
         return {
            ...state,
            transactions: action.payload,
            loading: false,
         };
      case REMOVE_TRANSACTION_CATEGORY:
         const updatedTransactionsRemove = state.transactions.map(
            (transaction) => {
               if (transaction.transactionId === action.payload) {
                  return {
                     ...transaction,
                     category: null,
                  };
               }
               return transaction;
            }
         );
         return {
            ...state,
            transactions: updatedTransactionsRemove,
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
