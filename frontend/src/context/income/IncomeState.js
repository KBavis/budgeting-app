import React, { useReducer } from "react";
import incomeReducer from "./incomeReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
   CREATE_INCOME_SUCCESS,
   CREATE_INCOME_FAIL,
   CLEAR_ERRORS,
   SET_LOADING,
   FETCH_INCOMES_SUCCESS,
   FETCH_INCOMES_FAIL,
} from "./types";
import initialState from "./initialState";
import IncomeContext from "./incomeContext";
import setAuthToken from "../../utils/setAuthToken";

/**
 * Global State for our Incomes
 *
 * @param props
 *    - props from App file
 * @returns
 */
const IncomeState = (props) => {
   const [state, dispatch] = useReducer(incomeReducer, initialState);

   /**
    * Functionality to Create a Single Income via REST API
    *
    * @param formData
    *     - payload to send to REST API
    */
   const addIncome = async (formData) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.post(`${apiUrl}/income`, formData, config);
         dispatch({ type: CREATE_INCOME_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: CREATE_INCOME_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    *  Functionality to fetch all Income entities corresponding to Authenticated User
    */
   const fetchIncomes = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(`${apiUrl}/income`);
         dispatch({ type: FETCH_INCOMES_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: FETCH_INCOMES_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to clear exisiting errors
    */
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   /**
    *  Functionality to set loading to true
    */
   const setLoading = () => dispatch({ type: SET_LOADING });

   // Return Income Global Provider
   return (
      <IncomeContext.Provider
         value={{
            incomes: state.incomes,
            loading: state.loading,
            error: state.error,
            addIncome,
            clearErrors,
            fetchIncomes,
            setLoading,
         }}
      >
         {props.children}
      </IncomeContext.Provider>
   );
};

export default IncomeState;
