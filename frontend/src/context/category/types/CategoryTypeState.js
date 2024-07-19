import React, { useReducer } from "react";
import categoryTypeReducer from "./categoryTypeReducer";
import axios from "axios";
import apiUrl from "../../../utils/url";
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
} from "./types";
import initialState from "./initialState";
import CategoryTypeContext from "./categoryTypeContext";
import setAuthToken from "../../../utils/setAuthToken";

/**
 * Global State for our global CategoryType state
 *
 * @param props
 *       - props from our main App file
 * @returns
 */
const CategoryTypeState = (props) => {
   const [state, dispatch] = useReducer(categoryTypeReducer, initialState);

   /**
    * Functionality to Bulk Create Category Types
    *
    * @param categoryTypes
    *          - payload consisting of CategoryTypes to send to REST API
    */
   const addCategoryTypes = async (categoryTypes) => {
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
            `${apiUrl}/category/type/bulk`,
            categoryTypes,
            config
         );
         dispatch({ type: CREATE_CATEGORY_TYPES_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: CREATE_CATEGORY_TYPES_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to Clear Exisiting Errors
    */
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   /**
    * Functionality to set loading to true
    */
   const setLoading = () => dispatch({ type: SET_LOADING });

   /**
    * Functionality to fetch all Category Types
    */
   const fetchCategoryTypes = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(`${apiUrl}/category/type`);
         dispatch({ type: FETCH_CATEGORY_TYPES_SUCCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: FETCH_CATEGORY_TYPES_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to update a CategoryType
    *
    * @param categoryTypeId
    *          - ID of CategoryType to update
    */
   const updateCategoryType = async (categoryTypeId, updateCategoryTypeDto) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const config = {
            headers: {
               "Content-Type": "application/json",
            },
         };

         const res = await axios.put(
            `${apiUrl}/category/type/${categoryTypeId}`,
            updateCategoryTypeDto,
            config
         );
         dispatch({ type: UPDATE_CATEGORY_TYPE_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: UPDATE_CATEGORY_TYPE_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   /**
    * Functionality to fetch a single CategoryType
    *
    * @param categoryTypeId
    *          - CategoryType ID to fetch
    */
   const fetchCategoryType = async (categoryTypeId) => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(
            `${apiUrl}/category/type/${categoryTypeId}`
         );
         console.log(res.data);
         dispatch({ type: FETCH_CATEGORY_TYPE_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: FETCH_CATEGORY_TYPE_FAILURE,
            payload: err.response.data.error,
         });
      }
   };

   return (
      <CategoryTypeContext.Provider
         value={{
            categoryTypes: state.categoryTypes,
            loading: state.loading,
            error: state.error,
            addCategoryTypes,
            clearErrors,
            fetchCategoryTypes,
            setLoading,
            updateCategoryType,
            fetchCategoryType,
         }}
      >
         {props.children}
      </CategoryTypeContext.Provider>
   );
};

export default CategoryTypeState;
