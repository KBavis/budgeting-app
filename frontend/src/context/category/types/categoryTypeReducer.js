import {
   CREATE_CATEGORY_TYPES_SUCCESS,
   CREATE_CATEGORY_TYPES_FAIL,
   CLEAR_ERRORS,
   FETCH_CATEGORY_TYPES_SUCCCESS,
   FETCH_CATEGORY_TYPES_FAIL,
   SET_LOADING,
   UPDATE_CATEGORY_TYPE_FAIL,
   UPDATE_CATEGORY_TYPE_SUCCESS,
} from "./types";

/**
 * Reducer to update CategoryType state based on specified actions
 */
export default (state, action) => {
   switch (action.type) {
      case CREATE_CATEGORY_TYPES_SUCCESS:
      case FETCH_CATEGORY_TYPES_SUCCCESS:
         return {
            ...state,
            categoryTypes: action.payload,
            loading: false,
            error: null,
         };
      case CREATE_CATEGORY_TYPES_FAIL:
      case FETCH_CATEGORY_TYPES_FAIL:
      case UPDATE_CATEGORY_TYPE_FAIL:
         return {
            ...state,
            error: action.payload,
            loading: false,
         };
      case UPDATE_CATEGORY_TYPE_SUCCESS:
         //Filter Out CategoryTypes Other Than Once To Upate
         const filteredCategoryTypes = state.categoryTypes.filter(
            (categoryType) =>
               categoryType.categoryTypeId !== action.payload.categoryTypeId
         );

         return {
            ...state,
            categoryTypes: [...filteredCategoryTypes, action.payload],
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
