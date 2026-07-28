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

/**
 * Reducer to update our Income State based on specified actions
 */
export default (state, action) => {
   switch (action.type) {
      case FETCH_INCOMES_SUCCESS: {
         const fetchedIncomes = Array.isArray(action.payload) ? action.payload : (action.payload ? [action.payload] : []);
         return {
            ...state,
            incomes: fetchedIncomes,
            loading: false,
            error: null,
            totalIncome: fetchedIncomes.reduce(
               (total, income) => total + (parseFloat(income?.amount) || 0),
               0
            ),
         };
      }
      case CREATE_INCOME_SUCCESS: {
         const existingIncomes = state.incomes || [];
         const newIncomes = action.payload ? [...existingIncomes, action.payload] : existingIncomes;
         return {
            ...state,
            incomes: newIncomes,
            loading: false,
            error: null,
            totalIncome: newIncomes.reduce(
               (total, income) => total + (parseFloat(income?.amount) || 0),
               0
            ),
         };
      }
      case UPDATE_INCOME_SUCCESS: {
         const targetId = action.payload?.incomeId;
         const updatedIncomesList = (state.incomes || []).map((inc) =>
            inc && targetId && String(inc.incomeId) === String(targetId) ? action.payload : inc
         );
         return {
            ...state,
            incomes: updatedIncomesList,
            loading: false,
            error: null,
            totalIncome: updatedIncomesList.reduce(
               (total, income) => total + (parseFloat(income?.amount) || 0),
               0
            ),
         };
      }
      case DELETE_INCOME_SUCCESS: {
         const targetId = typeof action.payload === 'object' ? action.payload?.incomeId : action.payload;
         const filteredIncomes = (state.incomes || []).filter(
            (inc) => inc && inc.incomeId && String(inc.incomeId) !== String(targetId)
         );
         return {
            ...state,
            incomes: filteredIncomes,
            loading: false,
            error: null,
            totalIncome: filteredIncomes.reduce(
               (total, income) => total + (parseFloat(income?.amount) || 0),
               0
            ),
         };
      }
      case CREATE_INCOME_FAIL:
      case FETCH_INCOMES_FAIL:
      case UPDATE_INCOME_FAIL:
      case DELETE_INCOME_FAIL:
         return {
            ...state,
            error: action.payload,
            loading: false,
         };
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
};
