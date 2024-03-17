import React, { useReducer, useContext } from "react";
import authReducer from "./authReducer";
import axios from "axios";
import apiUrl from "../../config/url";
import {
   REGISTER_FAIL,
   AUTH_FAIL,
   LOGOUT,
   AUTH_SUCCESS,
   REGISTER_SUCCESS,
} from "./types";
import initalState from "./initalState";
import AuthContext from "./authContext";

const AuthState = (props) => {
   const [state, dispatch] = useReducer(authReducer, initalState);

   //Register
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
         dispatch({ type: REGISTER_FAIL, payload: err.response.data });
      }
   };

   //Return Auth Global Provider
   return (
      <AuthContext.Provider
         value={{
            token: state.token,
            isAuthenticated: state.isAuthenticated,
            loading: state.loading,
            user: state.user,
            error: state.error,
            register,
         }}
      >
         {props.children}
      </AuthContext.Provider>
   );
};

export default AuthState;
