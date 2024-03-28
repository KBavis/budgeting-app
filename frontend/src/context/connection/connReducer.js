import { CONNECTION_FAILED, CONNECTION_CREATED, CLEAR_ERRORS } from "./types";

export default (state, action) => {
   switch (action.type) {
      case CONNECTION_CREATED:
         return {
            ...state,
            ...action.payload,
         };
      case CONNECTION_FAILED:
         localStorage.removeItem("token");
         return {
            ...state,
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
