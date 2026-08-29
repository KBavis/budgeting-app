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
   UPDATE_INCOME_SUCCESS,
   UPDATE_INCOME_FAIL,
   DELETE_INCOME_SUCCESS,
   DELETE_INCOME_FAIL,
} from "./types";
import initialState from "./initialState";
import IncomeContext from "./incomeContext";
import setAuthToken from "../../utils/setAuthToken";

/**
 * Global State for our Incomes
 */
const IncomeState = (props) => {
   const [state, dispatch] = useReducer(incomeReducer, initialState);

   /**
    * Functionality to Create a Single Income via REST API
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
         return res.data;
      } catch (err) {
         console.error(err);
         dispatch({
            type: CREATE_INCOME_FAIL,
            payload: err.response?.data?.error || "Failed to create income",
         });
         throw err;
      }
   };

   /**
    * Functionality to Update an existing Income via REST API
    */
   const updateIncome = async (updateData) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.patch(`${apiUrl}/income`, updateData, config);
         dispatch({ type: UPDATE_INCOME_SUCCESS, payload: res.data });
         return res.data;
      } catch (err) {
         console.error(err);
         dispatch({
            type: UPDATE_INCOME_FAIL,
            payload: err.response?.data?.error || "Failed to update income",
         });
         throw err;
      }
   };

   /**
    * Functionality to Delete an existing Income via REST API
    */
   const removeIncome = async (incomeId) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.delete(`${apiUrl}/income/${incomeId}`);
         dispatch({ type: DELETE_INCOME_SUCCESS, payload: incomeId });
         return res;
      } catch (err) {
         console.error("Error deleting income entity: ", err);
         dispatch({
            type: DELETE_INCOME_FAIL,
            payload: err.response?.data?.error || "Failed to delete income",
         });
         throw err;
      }
   };

   /**
    * Functionality to fetch all Income entities corresponding to Authenticated User
    */
   const fetchIncomes = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(`${apiUrl}/income`);
         dispatch({ type: FETCH_INCOMES_SUCCESS, payload: res.data });
         return res.data;
      } catch (err) {
         console.error(err);
         dispatch({
            type: FETCH_INCOMES_FAIL,
            payload: err.response?.data?.error || "Failed to fetch incomes",
         });
      }
   };

   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   const setLoading = () => dispatch({ type: SET_LOADING });

   return (
      <IncomeContext.Provider
         value={{
            incomes: state.incomes,
            loading: state.loading,
            error: state.error,
            totalIncome: state.totalIncome,
            addIncome,
            updateIncome,
            removeIncome,
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
