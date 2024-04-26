import {
  CREATE_INCOME_SUCCESS,
  CREATE_INCOME_FAIL,
  CLEAR_ERRORS,
} from "./types";

export default (state, action) => {
  switch (action.type) {
    case CREATE_INCOME_SUCCESS:
      return {
        ...state,
        incomes: state.incomes
          ? [...state.incomes, action.payload]
          : [action.payload],
        loading: false,
        error: null,
      };
    case CREATE_INCOME_FAIL:
      return {
        ...state,
        error: action.payload,
        loading: false,
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
