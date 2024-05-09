import {
   CREATE_CATEGORY_TYPES_SUCCESS,
   CREATE_CATEGORY_TYPES_FAIL,
   CLEAR_ERRORS,
} from "./types";

/**
 * Reducer to update CategoryType state based on specified actions
 */
export default (state, action) => {
   switch (action.type) {
      case CREATE_CATEGORY_TYPES_SUCCESS:
         return {
            ...state,
            categoryTypes: action.payload,
            loading: false,
            error: null,
         };
      case CREATE_CATEGORY_TYPES_FAIL:
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
      default:
         return state;
   }
};
