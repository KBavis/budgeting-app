import React, { useReducer } from "react";
import transactionReducer from "./transactionReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
   SYNC_TRANSACTIONS_SUCCESS,
   SYNC_TRANSACTIONS_FAIL,
   CLEAR_ERRORS,
} from "./types";
import initialState from "./initialState";
import TransactionContext from "./transactionContext";
import setAuthToken from "../../utils/setAuthToken";

/**
 * Transaction State to manage functionality with Transaction entieis
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

   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   return (
      <TransactionContext.Provider
         value={{
            transactions: state.transactions,
            loading: state.loading,
            error: state.error,
            syncTransactions,
            clearErrors,
         }}
      >
         {props.children}
      </TransactionContext.Provider>
   );
};

export default TransactionState;
