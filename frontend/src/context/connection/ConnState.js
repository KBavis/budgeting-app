import React, { useReducer, useContext } from "react";
import connReducer from "./connReducer";
import axios from "axios";
import apiUrl from "../../utils/url";
import { CLEAR_ERRORS, CONNECTION_FAILED, CONNECTION_CREATED } from "./types";
import initalState from "./initalState";

const ConnState = (props) => {
   const [state, dispatch] = useReducer(connReducer, initalState);

   //TODO: Should pass our account meta data and our public token to our server to 1) create connection, 2) create account
   //NOTE: Connection information (such as access token) should ONLY be stored on server, and not be accessible to user
   const createConnection = async (formData) => {
      const config = {
         headers: {
            "Content-Type": "application/json",
         },
      };

      try {
         const res = await axios.post(`${apiUrl}/connect`, formData, config);
         dispatch({ type: CONNECTION_CREATED, payload: res.data });
      } catch (err) {
         console.error(err);
         dispatch({ type: CONNECTION_FAILED, payload: err.response.data });
      }
   };

   //Clear Errors
   const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

   //Return Auth Global Provider
   return (
      <ConnContext.Provider
         value={{
            loading: state.loading,
            error: state.error,
            createConnection,
            clearErrors,
         }}
      >
         {props.children}
      </ConnContext.Provider>
   );
};

export default ConnState;
