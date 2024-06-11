import {
   AUTH_FAIL,
   AUTH_SUCCESS,
   REGISTER_FAIL,
   REGISTER_SUCCESS,
   LOGOUT,
   CLEAR_ERRORS,
   SET_LOADING,
   FETCH_AUTH_USER_FAIL,
   FETCH_AUTH_USER_SUCCESS,
} from "./types";

/**
 * Reducer for handling updating state for specific actions for Authentication
 */
export default (state, action) => {
   switch (action.type) {
      case AUTH_SUCCESS:
      case REGISTER_SUCCESS:
         localStorage.setItem("token", action.payload.token); //put JWT Token in Storage
         return {
            ...state,
            ...action.payload,
            isAuthenticated: true,
            loading: false,
         };
      case REGISTER_FAIL:
      case AUTH_FAIL:
      case LOGOUT:
      case FETCH_AUTH_USER_FAIL:
         localStorage.removeItem("token");
         return {
            ...state,
            token: null,
            isAuthenticated: false,
            loading: false,
            user: null,
            error: action.payload,
         };
      case FETCH_AUTH_USER_SUCCESS:
         return {
            ...state,
            user: action.payload,
            isAuthenticated: true,
            loading: false,
            token: localStorage.token,
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
