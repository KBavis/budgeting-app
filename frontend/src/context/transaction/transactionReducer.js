import {
   SYNC_TRANSACTIONS_SUCCESS,
   SYNC_TRANSACTIONS_FAIL,
   UPDATE_TRANSACTION_CATEGORY,
   CLEAR_ERRORS,
   REMOVE_TRANSACTION_CATEGORY,
   SET_LOADING,
   FETCH_TRANSACTIONS_FAIL,
   FETCH_TRANSACTIONS_SUCCESS,
   SPLIT_TRANSACTIONS_FAILURE,
   SPLIT_TRANSACTIONS_SUCCESS,
   ADD_TRANSACTION_SUCCESS,
   ADD_TRANSACTION_FAILURE,
   REDUCE_TRANSACTION_FAILURE,
   REDUCE_TRANSACTION_SUCCESS,
   DELETE_TRANSACTION_SUCCESS,
   DELETE_TRANSACTION_FAILURE,
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
      case ADD_TRANSACTION_SUCCESS:
         // Ensure state.transactions is not null
         const transactions = state.transactions || [];

         return {
            ...state,
            transactions: [...transactions, action.payload],
         };
      case DELETE_TRANSACTION_SUCCESS:
         // Ensure state.transactions is not null
         const stateTransactions = state.transactions || [];

         //Filter Transactions
         const filtered = stateTransactions.filter(
            (transaction) => transaction.transactionId != action.payload
         );

         return {
            ...state,
            transactions: filtered,
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
      case SPLIT_TRANSACTIONS_SUCCESS:
         const splitTransactionId = action.payload.originalTransactionId;
         const newSplitTransactions = action.payload.newTransactions;

         // Filter out the original transaction by ID
         const filteredTransactions = state.transactions.filter(
            (transaction) => transaction.transactionId !== splitTransactionId
         );

         // Add the new split transactions to the existing list
         const mergedTransactions = [
            ...filteredTransactions,
            ...newSplitTransactions,
         ];

         return {
            ...state,
            transactions: mergedTransactions,
            loading: false,
            error: null,
         };
      case REDUCE_TRANSACTION_SUCCESS:
         const { originalTransactionId, newTransaction } = action.payload;

         const reducedTransactions = state.transactions.map((transaction) => {
            if (transaction.transactionId === originalTransactionId) {
               return newTransaction;
            }
            return transaction;
         });

         return {
            ...state,
            transactions: reducedTransactions,
            loading: false,
            error: null,
         };
      case SPLIT_TRANSACTIONS_FAILURE:
      case SYNC_TRANSACTIONS_FAIL:
      case FETCH_TRANSACTIONS_FAIL:
      case ADD_TRANSACTION_FAILURE:
      case REDUCE_TRANSACTION_FAILURE:
      case DELETE_TRANSACTION_FAILURE:
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
