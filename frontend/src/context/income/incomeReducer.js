import {
   CREATE_INCOME_SUCCESS,
   CREATE_INCOME_FAIL,
   CLEAR_ERRORS,
   SET_LOADING,
   FETCH_INCOMES_SUCCESS,
   FETCH_INCOMES_FAIL,
} from "./types";

/**
 * Reducer to update our Income State based on specified actions
 */
export default (state, action) => {
   switch (action.type) {
      case CREATE_INCOME_SUCCESS:
      case FETCH_INCOMES_SUCCESS:
         return {
            ...state,
            incomes: state.incomes
               ? [...state.incomes, action.payload]
               : [action.payload],
            loading: false,
            error: null,
         };
      case CREATE_INCOME_FAIL:
      case FETCH_INCOMES_FAIL:
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
