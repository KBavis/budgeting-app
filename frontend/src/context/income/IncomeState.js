import React, { useReducer } from "react";
import incomeReducer from "./incomeReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
  CREATE_INCOME_SUCCESS,
  CREATE_INCOME_FAIL,
  CLEAR_ERRORS,
} from "./types";
import initialState from "./initialState";
import IncomeContext from "./incomeContext";
import setAuthToken from "../../utils/setAuthToken";

const IncomeState = (props) => {
  const [state, dispatch] = useReducer(incomeReducer, initialState);

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
      dispatch({ type: CREATE_INCOME_FAIL, payload: err.response.data.error });
    }
  };

  // Clear Errors
  const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

  // Return Income Global Provider
  return (
    <IncomeContext.Provider
      value={{
        incomes: state.incomes,
        loading: state.loading,
        error: state.error,
        addIncome,
        clearErrors,
      }}
    >
      {props.children}
    </IncomeContext.Provider>
  );
};

export default IncomeState;
