import React, { useContext, useReducer } from "react";
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
   REDUCE_TRANSACTION_FAILURE,
   REDUCE_TRANSACTION_SUCCESS,
   DELETE_TRANSACTION_FAILURE,
   DELETE_TRANSACTION_SUCCESS,
   RENAME_TRANSACTION_SUCCESS,
   RENAME_TRANSACTION_FAILURE,
   UPDATE_PREV_MONTH_TRANSACTION_CATEGORY,
   REMOVE_CATEGORY,
} from "./types";
import initialState from "./initialState";
import TransactionContext from "./transactionContext";
import setAuthToken from "../../utils/setAuthToken";
import AlertContext from "../alert/alertContext";
import accountContext from "../account/accountContext";

/**
 * Transaction State to manage functionality with Transaction entities
 *
 * @param props
 *          - properties from main App file
 */
const TransactionState = (props) => {
   const [state, dispatch] = useReducer(transactionReducer, initialState);
   const { setAlert } = useContext(AlertContext);
   const { updateAccountBalance } = useContext(accountContext)

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
         
         // update account balances if applicable 
         if (res.data && res.data.updatedAccounts) {
            updateAccountBalance(res.data.updatedAccounts)
         }
         
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
    * Functionality to update the state of Transactions to no longer correlate with remove Category
    *
    * @param categoryId
    *          - CategoryId corresponding to Category that needs to be removed
    */
   const removeCategory = async (categoryId) => {
      dispatch({
         type: REMOVE_CATEGORY,
         payload: categoryId,
      });
   };

   /**
    * Update a transaction's category on the frontend
    *
    * @param transactionId
    *          - ID of the transaction to update
    * @param categoryId
    *          - ID of the category to assign to the transaction
    */
   const updateCategory = async (transactionId, categoryId, isPrevMonth = false) => {
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

         if (isPrevMonth == false) {
            dispatch({
               type: UPDATE_TRANSACTION_CATEGORY,
               payload,
            });
         } else {
            dispatch({
               type: UPDATE_PREV_MONTH_TRANSACTION_CATEGORY,
               payload: transactionId
            })
         }

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
    * Function to reduce a Transaction amount
    *
    * @param transactionId
    *          - Transaction ID of Entitiy to be updated
    * @param amount
    *          - Reduced Transaction amount
    */
   const reduceTransactionAmount = async (transactionId, amount) => {
      try {
         if (localStorage.token) {
            setAuthToken(localStorage.token);
         }

         const config = {
            headers: {
               "Content-Type": "application/json",
            },
         };

         const transactionDto = {
            updatedAmount: amount,
         };

         const res = await axios.put(
            `${apiUrl}/transactions/${transactionId}`,
            transactionDto,
            config
         );

         console.log(res.data);
         dispatch({
            type: REDUCE_TRANSACTION_SUCCESS,
            payload: {
               originalTransactionId: transactionId,
               newTransaction: res.data,
            },
         });
      } catch (err) {
         console.error(err);
         dispatch({
            REDUCE_TRANSACTION_FAILURE,
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
         setAlert("Transaction successfully added", "success");
      } catch (err) {
         console.error(err);
         dispatch({
            ADD_TRANSACTION_FAILURE,
            payload: err.response.data.error,
         });
         setAlert(err.response.data.error, "danger");
      }
   };

   /**
    * Functionality to delete a Transaction
    *
    * @param transaction
    *          - Transaction to create
    */
   const deleteTransaction = async (transactionId) => {
      try {
         if (localStorage.token) {
            setAuthToken(localStorage.token);
         }

         await axios.delete(`${apiUrl}/transactions/${transactionId}`);
         dispatch({
            type: DELETE_TRANSACTION_SUCCESS,
            payload: transactionId,
         });
         setAlert("Transaction successfully deleted", "success");
      } catch (err) {
         console.error(err);
         dispatch({
            DELETE_TRANSACTION_FAILURE,
            payload: err.response.data.error,
         });
         setAlert(err.response.data.error, "danger");
      }
   };

   /**
    * Functionality to rename a Transaction
    *
    * @param transactionId
    *          - Transaction ID corresponding to Transaction to be renamed
    * @param updatedName
    *          - Updated Transaction name
    */
   const renameTransaction = async (transactionId, updatedName) => {
      try {
         if (localStorage.token) {
            setAuthToken(localStorage.token);
         }

         const payload = { transactionId, updatedName };

         const res = await axios.put(
            `${apiUrl}/transactions/${transactionId}/${updatedName}`
         );
         dispatch({ type: RENAME_TRANSACTION_SUCCESS, payload });
      } catch (err) {
         dispatch({
            RENAME_TRANSACTION_FAILURE,
            payload: err.response.data.error,
         });
         setAlert(err.response.data.error, "danger");
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
            prevMonthTransactions: state.prevMonthTransactions,
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
            reduceTransactionAmount,
            deleteTransaction,
            renameTransaction,
            removeCategory,
         }}
      >
         {props.children}
      </TransactionContext.Provider>
   );
};

export default TransactionState;
