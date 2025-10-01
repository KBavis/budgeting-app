import React, { useReducer } from "react";
import summaryReducer from "./summaryReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
    FETCH_BUDGET_SUMMARIES_SUCCESS,
    FETCH_BUDGET_SUMMARIES_ERROR,
    SET_PREV
} from "./types";
import { initalState } from "./initialState";
import SummaryContext from "./summaryContext";
import setAuthToken from "../../utils/setAuthToken";
import { CLEAR_ERRORS, SET_LOADING } from "../income/types";

/**
 * Global State for Budget Summaries
 *
 * @param props
 *          - props from App.js
 * @returns
 *          - SummaryState
 */
const SummaryState = (props) => {

    const [state, dispatch] = useReducer(summaryReducer, initalState);

    /**
     * Functionality to fetch users persisted BudgetSummaries
     */
    const fetchBudgetSummaries = async () => {
        if (localStorage.token) {
            setAuthToken(localStorage.token);
        }

        try {
            const res = await axios.get(`${apiUrl}/budget/performance/all`);
            dispatch({ type: FETCH_BUDGET_SUMMARIES_SUCCESS, payload: res.data });
        } catch (err) {
            console.error(err);
            dispatch({ type: FETCH_BUDGET_SUMMARIES_ERROR, payload: err.response.data.error });
        }
    }

    /**
     * Functionality to clear exisiting errors
     */
    const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

    /**
     *  Functionality to set loading to true
     */
    const setLoading = () => dispatch({ type: SET_LOADING });

    /**
     * Functionality to preserve history in active session for users selected budget summary
     *  
     * @param {summary} prev 
     *              - previous selected budget summary 
     * 
     */
    const setPrev = (prev) => {
        dispatch({
            type: SET_PREV,
            payload: prev
        })
    }

    return (
        <SummaryContext.Provider value={{
            summaries: state.summaries,
            loading: state.loading,
            error: state.error,
            prev: state.prev,
            fetchBudgetSummaries,
            setLoading,
            clearErrors,
            setPrev
        }}>
            {props.children}
        </SummaryContext.Provider>
    )
}

export default SummaryState;