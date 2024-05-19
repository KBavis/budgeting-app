import {
   CREATE_CATEGORIES_SUCCESS,
   CREATE_CATEGORIES_FAIL,
   CLEAR_ERRORS,
   FETCH_CATEGORIES_SUCCESS,
   FETCH_CATEGORIES_FAIL,
   SET_LOADING,
} from "./types";

/**
 * File to handle updating Category State based on different actions
 */
export default (state, action) => {
   switch (action.type) {
      case CREATE_CATEGORIES_SUCCESS:
      case FETCH_CATEGORIES_SUCCESS:
         return {
            ...state,
            categories: state.categories
               ? [...state.categories, ...action.payload]
               : action.payload,
            loading: false,
            error: null,
         };
      case CREATE_CATEGORIES_FAIL:
      case FETCH_CATEGORIES_FAIL:
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
