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
   RENAME_TRANSACTION_FAILURE,
   RENAME_TRANSACTION_SUCCESS,
   REMOVE_CATEGORY,
} from "./types";

export default (state, action) => {
   switch (action.type) {
      case SYNC_TRANSACTIONS_SUCCESS:

         //TODO: Delete me 
         console.log('Sync Transactions Response')
         console.log(action.payload)

         // New & Updated Transactions
         const newTransactions =
            action.payload.allModifiedOrAddedTransactions.reduce(
               (acc, transaction) => {
                  if (state.transactions) {
                     const existingTransactionIndex =
                        state.transactions.findIndex(
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

         // Remove Transactions
         const filteredTransactionsSync = newTransactions.filter(
            (transaction) =>
               !action.payload.removedTransactionIds.includes(
                  transaction.transactionId
               )
         );

         return {
            ...state,
            transactions: filteredTransactionsSync,
            prev_month_transactions: action.payload.previousMonthTransactions || [],
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
         const { transactionId, category } = action.payload;
         let transactionToUpdate = null;
         const remainingTransactions = state.transactions.filter(
            (transaction) => {
               if (transaction.transactionId === transactionId) {
                  transactionToUpdate = {
                     ...transaction,
                     category: { ...category },
                  };
                  return false;
               }
               return true;
            }
         );

         return {
            ...state,
            transactions: [transactionToUpdate, ...remainingTransactions],
         };
      case REMOVE_CATEGORY:
         //Find Transactions with this Category in state and update to be Null
         const removedCategoryTransactions = state.transactions
            ? state.transactions.map((transaction) =>
                 transaction.category?.categoryId === action.payload
                    ? { ...transaction, category: null }
                    : transaction
              )
            : [];

         return {
            ...state,
            transactions: removedCategoryTransactions,
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
            ...newSplitTransactions,
            ...filteredTransactions,
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
      case RENAME_TRANSACTION_FAILURE:
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
      case RENAME_TRANSACTION_SUCCESS:
         const renamedTransactions = state.transactions.map((transaction) => {
            if (transaction.transactionId === action.payload.transactionId) {
               return { ...transaction, name: action.payload.updatedName };
            }
            return transaction;
         });

         return {
            ...state,
            transactions: renamedTransactions,
            loading: false,
            error: null,
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
