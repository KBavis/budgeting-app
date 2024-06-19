import React, { useReducer, useContext } from "react";
import authReducer from "./authReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import {
   REGISTER_FAIL,
   AUTH_FAIL,
   LOGOUT,
   AUTH_SUCCESS,
   REGISTER_SUCCESS,
   CLEAR_ERRORS,
   SET_LOADING,
   FETCH_AUTH_USER_FAIL,
   FETCH_AUTH_USER_SUCCESS,
   LOGOUT_SUCCESS,
   LOGOUT_FAIL,
} from "./types";
import initalState from "./initalState";
import AuthContext from "./authContext";
import setAuthToken from "../../utils/setAuthToken";

/**
 * Global Authentication State
 *
 * @param props
 *      - props from our global App file
 * @returns
 */
const AuthState = (props) => {
   const [state, dispatch] = useReducer(authReducer, initalState);

   /**
    * Functionality to Register a new User
    *
    * @param formData
    *       - payload to send to our REST API
    */
   const register = async (formData) => {
      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };
      try {
         const res = await axios.post(
            `${apiUrl}/auth/register`,
            formData,
            config
         );
         dispatch({ type: REGISTER_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({ type: REGISTER_FAIL, payload: err.response.data.error });
      }
   };

   /**
    * Functionality to Authenticate User
    *
    * @param formData
    *       - payload to send to our REST API
    */
   const login = async (formData) => {
      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.post(
            `${apiUrl}/auth/authenticate`,
            formData,
            config
         );
         dispatch({ type: AUTH_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({ type: AUTH_FAIL, payload: err.response.data.error });
      }
   };

   /**
    * Function to fetch the currently authenticated user
    */
   const fetchAuthenticatedUser = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         const res = await axios.get(`${apiUrl}/auth`);
         dispatch({ type: FETCH_AUTH_USER_SUCCESS, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({
            type: FETCH_AUTH_USER_FAIL,
            payload: err.response.data.error,
         });
      }
   };

   const logout = async () => {
      if (localStorage.token) {
         setAuthToken(localStorage.token);
      }

      try {
         await axios.delete(`${apiUrl}/auth/logout`);
         dispatch({ type: LOGOUT_SUCCESS, payload: null });
      } catch (err) {
         console.error(err);
      }
   };

   /**
    * Functionality to Clear Errors
    */
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   /**
    *  Functionality to Set Loading
    */
   const setLoading = () => dispatch({ type: SET_LOADING });

   return (
      <AuthContext.Provider
         value={{
            token: state.token,
            isAuthenticated: state.isAuthenticated,
            loading: state.loading,
            user: state.user,
            error: state.error,
            register,
            login,
            clearErrors,
            setLoading,
            fetchAuthenticatedUser,
            logout,
         }}
      >
         {props.children}
      </AuthContext.Provider>
   );
};

export default AuthState;
