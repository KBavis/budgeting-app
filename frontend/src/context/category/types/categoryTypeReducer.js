import {
   CREATE_CATEGORY_TYPES_SUCCESS,
   CREATE_CATEGORY_TYPES_FAIL,
   CLEAR_ERRORS,
   FETCH_CATEGORY_TYPES_SUCCCESS,
   FETCH_CATEGORY_TYPES_FAIL,
   SET_LOADING,
   UPDATE_CATEGORY_TYPE_FAIL,
   UPDATE_CATEGORY_TYPE_SUCCESS,
   FETCH_CATEGORY_TYPE_FAILURE,
   FETCH_CATEGORY_TYPE_SUCCESS,
   REMOVE_CATEGORY,
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
      case REMOVE_CATEGORY:
         const removeCategoryCategoryTypes = state.categoryTypes
            ? state.categoryTypes.map((categoryType) => {
                 if (
                    categoryType.categoryTypeId ===
                    action.payload.categoryTypeId
                 ) {
                    return {
                       ...categoryType,
                       categories: categoryType.categories //Remove Category from 'categories' attribute
                          ? categoryType.categories.filter(
                               (category) =>
                                  category.categoryId !==
                                  action.payload.categoryId
                            )
                          : [],
                    };
                 } else {
                    return categoryType;
                 }
              })
            : [];

         return {
            ...state,
            categoryTypes: removeCategoryCategoryTypes,
         };

      case FETCH_CATEGORY_TYPE_SUCCESS:
         const categoryTypes = state.categoryTypes.filter(
            (categoryType) =>
               categoryType.categoryTypeId !== action.payload.categoryTypeId
         );

         return {
            ...state,
            categoryTypes: [...categoryTypes, action.payload],
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
