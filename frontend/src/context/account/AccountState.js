import React, { useReducer, useContext } from "react";
import connReducer from "./accountReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import initalState from "./initialState";
import {
   ACCOUNT_CREATED,
   ACCOUNT_FAILED_CREATED,
   ACCOUNTS_FETCHED,
   ACCOUNTS_FETCH_FAILED,
   CLEAR_ERRORS,
   SET_LOADING, REMOVE_ACCOUNT_FAILURE, REMOVE_ACCOUNT_SUCCESS,
   UPDATE_ACCOUNT_BALANCE
} from "./types";
import setAuthToken from "../../utils/setAuthToken";
import AccountContext from "./accountContext";
import accountContext from "./accountContext";
import alertContext from "../alert/alertContext";

/**
 * File to store the global state for our Account
 *
 * @param props
 *       - props from main App file
 */
const AccountState = (props) => {
   const [state, dispatch] = useReducer(connReducer, initalState);
   const { setAlert } = useContext(alertContext);

   /**
    * Functionality to create account via REST API
    *
    * @param formData
    *       - payload to send to API
    */
   const createAccount = async (formData) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.post(`${apiUrl}/account`, formData, config);
         dispatch({
            type: ACCOUNT_CREATED,
            payload: res.data,
         });
      } catch (err) {
         console.error(err);
         dispatch({
            type: ACCOUNT_FAILED_CREATED,
            payload: err.response.data.error,
         });
      }
   };

   /**
    *  Functionality to clear any errors that may have occured
    */
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   const setLoading = () => dispatch({ type: SET_LOADING });

   /**
    * Functionality to fetch all accounts associated with authenticated user
    */
   const fetchAccounts = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(`${apiUrl}/account`);
         dispatch({
            type: ACCOUNTS_FETCHED,
            payload: res.data,
         });
      } catch (err) {
         console.error(err);
         dispatch({
            type: ACCOUNTS_FETCH_FAILED,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to remove an Account from state
    *
    * @param accountId
    *          - Account ID to remove from state
    */
   const removeAccount = async (accountId) => {
      if(localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         await axios.delete(`${apiUrl}/account/${accountId}`);
         dispatch({type: REMOVE_ACCOUNT_SUCCESS, payload: accountId});
         setAlert("Account successfully deleted", "success");
      } catch (err) {
         console.error(err);
         dispatch({
            type: REMOVE_ACCOUNT_FAILURE,
            payload: err.response.data.error
         })
      }
   }


   /**
    * Functionality to update acocunts with most recent balances retrieved during Sync Transactions flow
    * 
    * @param accounts 
    *       - list of account DTOs with potential balance updates
    */
   const updateAccountBalance = async (accounts) => {
         dispatch({
            type: UPDATE_ACCOUNT_BALANCE, 
            payload: accounts
         })
   }

   return (
      <AccountContext.Provider
         value={{
            accounts: state.accounts,
            loading: state.loading,
            error: state.error,
            createAccount,
            clearErrors,
            fetchAccounts,
            setLoading,
            removeAccount,
            updateAccountBalance
         }}
      >
         {props.children}
      </AccountContext.Provider>
   );
};

export default AccountState;
