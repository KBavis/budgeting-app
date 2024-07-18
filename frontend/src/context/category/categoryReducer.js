import {
   CREATE_CATEGORIES_SUCCESS,
   CREATE_CATEGORIES_FAIL,
   CLEAR_ERRORS,
   FETCH_CATEGORIES_SUCCESS,
   FETCH_CATEGORIES_FAIL,
   SET_LOADING,
   CREATE_CATEGORY_FAIL,
   CREATE_CATEGORY_SUCCESS,
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
      case CREATE_CATEGORY_FAIL:
      case CREATE_CATEGORIES_FAIL:
      case FETCH_CATEGORIES_FAIL:
         return {
            ...state,
            error: action.payload,
            loading: false,
         };
      case CREATE_CATEGORY_SUCCESS:
         //Add New Category
         const newCategories = [
            ...state.categories,
            action.payload.newCategory,
         ];

         //Update Existing Categories BudgetPercent/BudgetAmount
         const categoriesWithUpdates = action.payload.categoriesWithUpdates;
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

         return {
            ...state,
            categories: updatedCategories,
            loading: false,
            error: null,
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
