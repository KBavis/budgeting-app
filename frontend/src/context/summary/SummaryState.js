import React, { useReducer, useContext } from "react";
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
import authContext from "../auth/authContext";
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
    const { user } = useContext(authContext);

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
            dispatch({ type: FETCH_BUDGET_SUMMARIES_ERROR, payload: err.response?.data?.error || 'Failed to fetch budget summaries' });
        }
    };

    /**
     * Functionality to clear existing errors
     */
    const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

    /**
     * Functionality to set loading to true
     */
    const setLoading = () => dispatch({ type: SET_LOADING });

    /**
     * Functionality to preserve history in active session for users selected budget summary
     *  
     * @param {summary} prev 
     *              - previous selected budget summary 
     */
    const setPrev = (prev) => {
        dispatch({
            type: SET_PREV,
            payload: prev
        });
    };

    /**
     * Re-creates/recalculates the Budget Overview summary for a given past month and year,
     * enforcing the required userId query parameter.
     *
     * @param {number|string} month
     * @param {number|string} year
     * @param {number|string} [targetUserId] - Optional user ID override, defaults to authContext user.userId
     */
    const recalculateBudgetSummary = async (month, year, targetUserId) => {
        if (localStorage.token) {
            setAuthToken(localStorage.token);
        }

        const effectiveUserId = targetUserId || user?.userId;
        if (!effectiveUserId) {
            console.error("User ID is required to recalculate budget summary");
            return;
        }

        const monthNames = [
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        ];
        let formattedMonth = month;
        if (typeof month === 'number' || (typeof month === 'string' && !isNaN(parseInt(month, 10)))) {
            const mNum = parseInt(month, 10);
            formattedMonth = monthNames[mNum - 1] || String(month).toUpperCase();
        } else if (typeof month === 'string') {
            formattedMonth = month.trim().toUpperCase();
        }

        try {
            await axios.post(`${apiUrl}/budget/performance/recalculate?userId=${effectiveUserId}`, {
                month: formattedMonth,
                year: parseInt(year, 10)
            });
            await fetchBudgetSummaries();
        } catch (err) {
            console.error("Error recalculating budget summary:", err);
            await fetchBudgetSummaries();
        }
    };

    return (
        <SummaryContext.Provider value={{
            summaries: state.summaries,
            loading: state.loading,
            error: state.error,
            prev: state.prev,
            fetchBudgetSummaries,
            recalculateBudgetSummary,
            setLoading,
            clearErrors,
            setPrev
        }}>
            {props.children}
        </SummaryContext.Provider>
    );
};

export default SummaryState;