import React, { useReducer } from "react";
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
} from "./types";
import initialState from "./initialState";
import CategoryContext from "./categoryContext";
import setAuthToken from "../../utils/setAuthToken";

/**
 * Global State for our Categories
 *
 * @param props
 *    - Props from our main App file
 * @returns
 */
const CategoryState = (props) => {
   const [state, dispatch] = useReducer(categoryReducer, initialState);

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
         }}
      >
         {props.children}
      </CategoryContext.Provider>
   );
};

export default CategoryState;
