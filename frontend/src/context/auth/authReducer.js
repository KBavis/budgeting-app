import {
   AUTH_FAIL,
   AUTH_SUCCESS,
   REGISTER_FAIL,
   REGISTER_SUCCESS,
   LOGOUT,
   CLEAR_ERRORS,
} from "./types";

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
         localStorage.removeItem("token");
         return {
            ...state,
            token: null,
            isAuthenticated: false,
            loading: false,
            user: null,
            error: action.payload,
         };
      case CLEAR_ERRORS:
         return {
            ...state,
            error: null,
         };
      default:
         return state;
   }
};
