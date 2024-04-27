import React, { useReducer } from "react";
import categoryTypeReducer from "./categoryTypeReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
   CREATE_CATEGORY_TYPES_SUCCESS,
   CREATE_CATEGORY_TYPES_FAIL,
   CLEAR_ERRORS,
} from "./types";
import initialState from "./initialState";
import CategoryTypeContext from "./categoryTypeContext";
import setAuthToken from "../../utils/setAuthToken";

const CategoryTypeState = (props) => {
   const [state, dispatch] = useReducer(categoryTypeReducer, initialState);

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

   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   return (
      <CategoryTypeContext.Provider
         value={{
            categoryTypes: state.categoryTypes,
            loading: state.loading,
            error: state.error,
            addCategoryTypes,
            clearErrors,
         }}
      >
         {props.children}
      </CategoryTypeContext.Provider>
   );
};

export default CategoryTypeState;
