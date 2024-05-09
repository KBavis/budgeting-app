import React, { useReducer, useContext } from "react";
import connReducer from "./accountReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import initalState from "./initialState";
import {
   ACCOUNT_CREATED,
   ACCOUNT_DELETED,
   ACCOUNT_FAILED_CRATED,
   ACCOUNT_FAILED_DELETED,
   CLEAR_ERRORS,
} from "./types";
import setAuthToken from "../../utils/setAuthToken";
import AccountContext from "./accountContext";

/**
 * File to store the global state for our Account
 *
 * @param props
 *       - props from main App file
 */
const AccountState = (props) => {
   const [state, dispatch] = useReducer(connReducer, initalState);

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
         dispatch({ type: ACCOUNT_CREATED, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: ACCOUNT_FAILED_CRATED,
            payload: err.response.data.error,
         });
      }
   };

   /**
    *  Functionality to clear any errors that may have occured
    */
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   return (
      <AccountContext.Provider
         value={{
            loading: state.loading,
            error: state.error,
            createAccount,
            clearErrors,
         }}
      >
         {props.children}
      </AccountContext.Provider>
   );
};

export default AccountState;
