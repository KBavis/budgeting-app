import React, { useReducer } from "react";
import categoryReducer from "./categoryReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
  CREATE_CATEGORIES_SUCCESS,
  CREATE_CATEGORIES_FAIL,
  CLEAR_ERRORS,
} from "./types";
import initialState from "./initialState";
import CategoryContext from "./categoryContext";
import setAuthToken from "../../utils/setAuthToken";

const CategoryState = (props) => {
  const [state, dispatch] = useReducer(categoryReducer, initialState);

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

  const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

  return (
    <CategoryContext.Provider
      value={{
        categories: state.categories,
        loading: state.loading,
        error: state.error,
        addCategories,
        clearErrors,
      }}
    >
      {props.children}
    </CategoryContext.Provider>
  );
};

export default CategoryState;