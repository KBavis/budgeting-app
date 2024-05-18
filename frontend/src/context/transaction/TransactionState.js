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
    * Update a transaction's category on the frontend
    *
    * @param transactionId
    *          - ID of the transaction to update
    * @param categoryId
    *          - ID of the category to assign to the transaction
    */
   const updateCategory = (transactionId, categoryId) => {
      console.log(
         `In updateCategory() in TransactionState with following TransactionId & CategoryId: [${transactionId}, ${categoryId}]`
      );
      const category = { categoryId };
      const payload = { transactionId, category };
      console.log(`Sending the following payload over to reducer: ${payload}`);
      dispatch({
         type: UPDATE_TRANSACTION_CATEGORY,
         payload,
      });

      // TODO: Implement the backend API call to update the transaction's category
      // You can use the following code as a starting point:
      // try {
      //   const response = await axios.put(
      //     `${apiUrl}/transactions/${transactionId}/category`,
      //     { categoryId }
      //   );
      //   console.log("Transaction category updated successfully:", response.data);
      // } catch (error) {
      //   console.error("Error updating transaction category:", error);
      // }
   };

   /**
    * Update a transaction's category on the frontend
    *
    * @param transactionId
    *          - ID of the transaction to update
    * @param categoryId
    *          - ID of the category to assign to the transaction
    */
   const removeTransactionCategory = (transactionId) => {
      console.log("in removeCategory in state");
      console.log(transactionId);
      dispatch({
         type: REMOVE_TRANSACTION_CATEGORY,
         payload: transactionId,
      });

      // TODO: Implement the backend API call to remove the transaction's category
      // You can use the following code as a starting point:
      // try {
      //   const response = await axios.put(
      //     `${apiUrl}/transactions/${transactionId}/category`,
      //     { categoryId }
      //   );
      //   console.log("Transaction category updated successfully:", response.data);
      // } catch (error) {
      //   console.error("Error updating transaction category:", error);
      // }
   };

   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   return (
      <TransactionContext.Provider
         value={{
            transactions: state.transactions,
            loading: state.loading,
            error: state.error,
            syncTransactions,
            updateCategory,
            removeTransactionCategory,
            clearErrors,
         }}
      >
         {props.children}
      </TransactionContext.Provider>
   );
};

export default TransactionState;
