import initialState from "./initalState";
import React, { useReducer } from "react";
import { v4 as uuidv4 } from "uuid";
import { SET_ALERT, REMOVE_ALERT } from "./types";
import AlertContext from "./alertContext";
import alertReducer from "./alertReducer";

const AlertState = (props) => {
   const [state, dispatch] = useReducer(alertReducer, initialState);

   /**
    * Set an alert (on success or error)
    *
    * @param {string} msg - message of alert
    * @param {string} type - type of alert
    */
   const setAlert = (msg, type) => {
      const id = uuidv4(); // Generate a random UUID for the alert ID

      dispatch({
         type: SET_ALERT,
         payload: { msg, type, id },
      });

      // Remove Alert after 5 seconds
      setTimeout(() => dispatch({ type: REMOVE_ALERT, payload: id }), 5000);
   };

   /**
    * Alert Provider
    */
   return (
      <AlertContext.Provider
         value={{
            alerts: state,
            setAlert,
         }}
      >
         {props.children}
      </AlertContext.Provider>
   );
};

export default AlertState;
