import {
   RENAME_CATEGORY_FAIL,
   CREATE_CATEGORIES_SUCCESS,
   CREATE_CATEGORIES_FAIL,
   CLEAR_ERRORS,
   FETCH_CATEGORIES_SUCCESS,
   FETCH_CATEGORIES_FAIL,
   SET_LOADING,
   CREATE_CATEGORY_FAIL,
   CREATE_CATEGORY_SUCCESS,
   UPDATE_CATEGORY_ALLOCATIONS_FAIL,
   UPDATE_CATEGORY_ALLOCATIONS_SUCCESS,
   DELETE_CATEGORY_SUCCESS,
   RENAME_CATEGORY_SUCCESS,
} from "./types";

/**
 * File to handle updating Category State based on different actions
 */
export default (state, action) => {
   switch (action.type) {
      case FETCH_CATEGORIES_SUCCESS:
         return {
            ...state,
            categories: Array.isArray(action.payload) ? action.payload : [],
            loading: false,
            error: null,
         };
      case CREATE_CATEGORIES_SUCCESS: {
         const existing = state.categories || [];
         const payloadArr = Array.isArray(action.payload) ? action.payload : [action.payload];
         const map = new Map();
         [...existing, ...payloadArr].forEach((c) => {
            if (c && c.categoryId) map.set(c.categoryId, c);
         });
         return {
            ...state,
            categories: Array.from(map.values()),
            loading: false,
            error: null,
         };
      }
      case RENAME_CATEGORY_FAIL:
      case CREATE_CATEGORY_FAIL:
      case CREATE_CATEGORIES_FAIL:
      case FETCH_CATEGORIES_FAIL:
      case UPDATE_CATEGORY_ALLOCATIONS_FAIL:
         return {
            ...state,
            error: action.payload,
            loading: false,
         };
      case DELETE_CATEGORY_SUCCESS: {
         const updates = state.categories
            ? state.categories.filter(
                 (category) => category.categoryId !== action.payload
              )
            : [];
         return {
            ...state,
            loading: false,
            categories: updates,
         };
      }
      case UPDATE_CATEGORY_ALLOCATIONS_SUCCESS: {
         const filteredCategories = (state.categories || []).filter(
            (category) =>
               !action.payload.some(
                  (updatedCategory) =>
                     updatedCategory.categoryId === category.categoryId
               )
         );

         const updatedStateCategories = [
            ...filteredCategories,
            ...action.payload,
         ];

         return {
            ...state,
            categories: updatedStateCategories,
            loading: false,
            error: null,
         };
      }
      case CREATE_CATEGORY_SUCCESS: {
         const newCategories = [
            ...(state.categories || []),
            action.payload.newCategory,
         ];

         const categoriesWithUpdates = action.payload.categoriesWithUpdates || [];
         const updatedCategories = newCategories.map((category) => {
            const categoryToUpdate = categoriesWithUpdates.find(
               (c) => c.categoryId === category.categoryId
            );

            if (categoryToUpdate) {
               return {
                  ...category,
                  budgetAmount: categoryToUpdate.budgetAmount,
                  budgetAllocationPercentage:
                     categoryToUpdate.budgetAllocationPercentage,
               };
            }
            return category;
         });

         // Deduplicate
         const map = new Map();
         updatedCategories.forEach((c) => {
            if (c && c.categoryId) map.set(c.categoryId, c);
         });

         return {
            ...state,
            categories: Array.from(map.values()),
            loading: false,
            error: null,
         };
      }
      case RENAME_CATEGORY_SUCCESS: {
         const categoriesWithRenamed = state.categories?.map((category) =>
            category.categoryId === action.payload.categoryId ? action.payload : category
         );
         return {
            ...state,
            categories: categoriesWithRenamed,
            loading: false,
         };
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
};
