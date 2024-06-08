import React, { useReducer } from "react";
import transactionReducer from "./transactionReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
   SYNC_TRANSACTIONS_SUCCESS,
   SYNC_TRANSACTIONS_FAIL,
   UPDATE_TRANSACTION_CATEGORY,
   CLEAR_ERRORS,
   REMOVE_TRANSACTION_CATEGORY,
   SET_LOADING,
   FETCH_TRANSACTIONS_FAIL,
   FETCH_TRANSACTIONS_SUCCESS,
   UPDATE_TRANSACTION_CATEGORY_FAILED,
   REMOVE_TRANSACTION_CATEGORY_FAILED,
   SPLIT_TRANSACTIONS_FAILURE,
   SPLIT_TRANSACTIONS_SUCCESS,
   ADD_TRANSACTION_SUCCESS,
   ADD_TRANSACTION_FAILURE,
} from "./types";
import initialState from "./initialState";
import TransactionContext from "./transactionContext";
import setAuthToken from "../../utils/setAuthToken";

/**
 * Transaction State to manage functionality with Transaction entities
 *
 * @param props
 *          - properties from main App file
 */
const TransactionState = (props) => {
   const [state, dispatch] = useReducer(transactionReducer, initialState);

   /**
    * Functionality to sync transactions with external financial institutions
    *
    * @param accounts
    *          - list of account IDs to sync transactions for
    */
   const syncTransactions = async (accounts) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.post(
            `${apiUrl}/transactions/sync`,
            { accounts },
            config
         );

         dispatch({
            type: SYNC_TRANSACTIONS_SUCCESS,
            payload: res.data,
         });
      } catch (err) {
         console.error(err);
         dispatch({
            type: SYNC_TRANSACTIONS_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to fetch all corresponding transactions persisted for authenticated user within current month
    */
   const fetchTransactions = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(`${apiUrl}/transactions`);
         dispatch({ type: FETCH_TRANSACTIONS_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: FETCH_TRANSACTIONS_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Update a transaction's category on the frontend
    *
    * @param transactionId
    *          - ID of the transaction to update
    * @param categoryId
    *          - ID of the category to assign to the transaction
    */
   const updateCategory = async (transactionId, categoryId) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.put(
            `${apiUrl}/transactions/category`,
            {
               transactionId,
               categoryId,
            },
            config
         );
         const data = res.data;
         const { category } = data;
         const payload = { transactionId, category };
         dispatch({
            type: UPDATE_TRANSACTION_CATEGORY,
            payload,
         });
      } catch (err) {
         console.error(err);
         dispatch({
            type: UPDATE_TRANSACTION_CATEGORY_FAILED,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Update a transaction's category on the frontend
    *
    * @param transactionId
    *          - ID of the transaction to update
    * @param categoryId
    *          - ID of the category to assign to the transaction
    */
   const removeTransactionCategory = async (transactionId) => {
      try {
         if (localStorage.token) {
            setAuthToken(localStorage.token);
         }

         await axios.delete(`${apiUrl}/transactions/${transactionId}/category`);
         dispatch({
            type: REMOVE_TRANSACTION_CATEGORY,
            payload: transactionId,
         });
      } catch (err) {
         console.error(err);
         dispatch({
            REMOVE_TRANSACTION_CATEGORY_FAILED,
            payload: err.response.data.error,
         });
      }
   };

   /**
    *
    * @param transactionId
    *          - ID of original Transaction entitiy being split out
    * @param splitTransactions
    *          - List of TransactionDto to split original Transaction into
    */
   const splitTransaction = async (transactionId, splitTransactions) => {
      try {
         if (localStorage.token) {
            setAuthToken(localStorage.token);
         }

         const config = {
            headers: {
               "Content-Type": "application/json",
            },
         };

         console.log(splitTransactions);

         const res = await axios.put(
            `${apiUrl}/transactions/${transactionId}/split`,
            splitTransactions,
            config
         );

         console.log(res.data);
         dispatch({
            type: SPLIT_TRANSACTIONS_SUCCESS,
            payload: {
               originalTransactionId: transactionId,
               newTransactions: res.data,
            },
         });
      } catch (err) {
         console.error(err);
         dispatch({
            SPLIT_TRANSACTIONS_FAILURE,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to create a Transaction independent from any Account
    *
    * @param transaction
    *          - Transaction to create
    */
   const addTransaction = async (transaction) => {
      try {
         if (localStorage.token) {
            setAuthToken(localStorage.token);
         }

         const config = {
            headers: {
               "Content-Type": "application/json",
            },
         };

         const res = await axios.post(
            `${apiUrl}/transactions`,
            transaction,
            config
         );

         console.log(res.data);
         dispatch({
            type: ADD_TRANSACTION_SUCCESS,
            payload: res.data,
         });
      } catch (err) {
         console.error(err);
         dispatch({
            ADD_TRANSACTION_FAILURE,
            payload: err.response.data.error,
         });
      }
   };

   /**
    *  Functionality to clear errors
    */
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   /**
    * Functionality to set loading
    */
   const setLoading = () => dispatch({ type: SET_LOADING });

   return (
      <TransactionContext.Provider
         value={{
            transactions: state.transactions,
            loading: state.loading,
            error: state.error,
            syncTransactions,
            updateCategory,
            removeTransactionCategory,
            fetchTransactions,
            clearErrors,
            setLoading,
            splitTransaction,
            addTransaction,
         }}
      >
         {props.children}
      </TransactionContext.Provider>
   );
};

export default TransactionState;
