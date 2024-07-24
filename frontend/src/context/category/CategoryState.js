import React, { useReducer, useContext } from "react";
import categoryReducer from "./categoryReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
   CREATE_CATEGORIES_SUCCESS,
   CREATE_CATEGORIES_FAIL,
   CLEAR_ERRORS,
   SET_LOADING,
   FETCH_CATEGORIES_SUCCESS,
   FETCH_CATEGORIES_FAIL,
   CREATE_CATEGORY_SUCCESS,
   CREATE_CATEGORY_FAIL,
   UPDATE_CATEGORY_ALLOCATIONS_FAIL,
   UPDATE_CATEGORY_ALLOCATIONS_SUCCESS,
   DELETE_CATEGORY_FAIL,
   DELETE_CATEGORY_SUCCESS,
} from "./types";
import initialState from "./initialState";
import CategoryContext from "./categoryContext";
import setAuthToken from "../../utils/setAuthToken";
import AlertContext from "../alert/alertContext";

/**
 * Global State for our Categories
 *
 * @param props
 *    - Props from our main App file
 * @returns
 */
const CategoryState = (props) => {
   const [state, dispatch] = useReducer(categoryReducer, initialState);
   const { setAlert } = useContext(AlertContext);

   /**
    * Functionality to Bulk Create Categories via REST API
    *
    * @param categories
    *     - payload consisting of cateogies to add
    */
   const addCategories = async (categories) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.post(
            `${apiUrl}/category/bulk`,
            { categories },
            config
         );
         dispatch({ type: CREATE_CATEGORIES_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: CREATE_CATEGORIES_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    *  Functionality to fetch all Categories corresponding to authenticated user
    */
   const fetchCategories = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(`${apiUrl}/category`);
         dispatch({ type: FETCH_CATEGORIES_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: FETCH_CATEGORIES_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to update Category budget allocations
    *
    * @param updateCategorytDtos
    *       - DTOs containing modifications to Category allocations
    */
   const updateAllocations = async (updateCategorytDtos, categoryTypeId) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      const requestBody = {
         updatedCategories: updateCategorytDtos,
         categoryTypeId,
      };

      try {
         const res = await axios.put(`${apiUrl}/category`, requestBody, config);

         dispatch({
            type: UPDATE_CATEGORY_ALLOCATIONS_SUCCESS,
            payload: res.data,
         });
         setAlert("Category allocations updated", "success");
      } catch (err) {
         console.error(err);
         dispatch({
            type: UPDATE_CATEGORY_ALLOCATIONS_FAIL,
            payload: err.response.data.error,
         });
         setAlert(err.response.data.error, "danger");
      }
   };

   /**
    * Functionality to remove a Category
    *
    * @param categoryId
    */
   const deleteCategory = async (categoryId) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.delete(`${apiUrl}/category/${categoryId}`);
         dispatch({
            type: DELETE_CATEGORY_SUCCESS,
            payload: categoryId,
         });
      } catch (err) {
         console.error(err);
         dispatch({
            type: DELETE_CATEGORY_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to add a new Category to a CategoryType and adjust other Categories allocations as needed
    */
   const addCategory = async (
      addedCategory,
      updatedCategories,
      categoryType
   ) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      const requestBody = { updatedCategories, addedCategory };
      console.log(requestBody);

      try {
         const res = await axios.post(
            `${apiUrl}/category`,
            requestBody,
            config
         );

         //Adjust List<UpdateCategoryDto> with new 'budgetAmount' attribute
         let categoriesWithUpdates = updatedCategories.map(
            (updateCategoryDto) => ({
               ...updateCategoryDto,
               budgetAmount: calculateAdjustedCategoryAmount(
                  updateCategoryDto,
                  categoryType
               ),
            })
         );
         let payload = { newCategory: res.data, categoriesWithUpdates };

         dispatch({
            type: CREATE_CATEGORY_SUCCESS,
            payload,
         });
         setAlert("Category created successfully", "success");
      } catch (err) {
         console.error(err);
         dispatch({
            type: CREATE_CATEGORY_FAIL,
            payload: err.response.data.error,
         });
         setAlert(err.response.data.error, "danger");
      }
   };

   /**
    * Utility function to determine adjusted 'amountAllocated' for given Category
    *
    * @param updateCategoryDto
    *          - UpdateCategoryDto (categoryId, budgetAllocationPercentage)
    * @param categoryType
    *          - CategoryType pertaining to Categories being adjusted
    */
   const calculateAdjustedCategoryAmount = (
      updateCategoryDto,
      categoryType
   ) => {
      return (
         categoryType.budgetAmount *
         updateCategoryDto.budgetAllocationPercentage
      );
   };

   /**
    *  Functionality to set the loading to true
    */
   const setLoading = () => dispatch({ type: SET_LOADING });

   /**
    * Functionality to handle clearing all existing errors
    */
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   return (
      <CategoryContext.Provider
         value={{
            categories: state.categories,
            loading: state.loading,
            error: state.error,
            addCategories,
            clearErrors,
            fetchCategories,
            setLoading,
            addCategory,
            updateAllocations,
            deleteCategory,
         }}
      >
         {props.children}
      </CategoryContext.Provider>
   );
};

export default CategoryState;
