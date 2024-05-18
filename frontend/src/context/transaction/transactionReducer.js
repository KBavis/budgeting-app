import {
   SYNC_TRANSACTIONS_SUCCESS,
   SYNC_TRANSACTIONS_FAIL,
   UPDATE_TRANSACTION_CATEGORY,
   CLEAR_ERRORS,
   REMOVE_TRANSACTION_CATEGORY,
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
         console.log(
            `In transactionReducer for action type UPDATE_TRANSACTION_CATEGORY with following payload`
         );
         console.log(action.payload);
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
         return {
            ...state,
            error: action.payload,
            loading: false,
         };
      case REMOVE_TRANSACTION_CATEGORY:
         const updatedTransactionsRemove = state.transactions.map(
            (transaction) => {
               if (transaction.transactionId === action.payload) {
                  return {
                     ...transaction,
                     category: {
                        ...transaction.category,
                        categoryId: null, // Set categoryId to null within the category object
                     },
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
      default:
         return state;
   }
};
