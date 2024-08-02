import {
    FETCH_BUDGET_SUMMARIES_ERROR,
    FETCH_BUDGET_SUMMARIES_SUCCESS
} from "./types";
import {CLEAR_ERRORS, SET_LOADING} from "../income/types";

/**
 * Reducer for up
 * @param state initial state
 * @param action value containing payload
 * @returns updated summary state
 */
export default (state,action) => {
    switch (action.type) {
        case FETCH_BUDGET_SUMMARIES_SUCCESS:
            return {
                ...state,
                summaries: action.payload,
                error: null,
                loading: false
            }
        case FETCH_BUDGET_SUMMARIES_ERROR:
            return {
                ...state,
                error: action.payload,
                loading: false
            }
        case CLEAR_ERRORS:
            return {
                ...state,
                error: null,
            };
        case SET_LOADING:
            return {
                ...state,
                loading: true,
            };
        default:
            return state;
    }
}