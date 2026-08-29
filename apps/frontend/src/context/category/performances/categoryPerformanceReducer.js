import {
    FETCH_CATEGORY_PERFORMANCES_SUCCESS,
    FETCH_CATEGORY_PERFORMANCES_ERROR,
    SET_LOADING,
    CLEAR_ERRORS
} from "./types"



export default (state, action) => {
    switch (action.type) {
        case FETCH_CATEGORY_PERFORMANCES_SUCCESS:
            return {
                ...state,
                category_performances: action.payload,
                error: null,
                loading: false
            }
        case FETCH_CATEGORY_PERFORMANCES_ERROR:
            return {
                ...state,
                error: action.payload,
                loading: false
            }
        case CLEAR_ERRORS:
            return {
                ...state,
                error: null
            }
        case SET_LOADING:
            return {
                ...state,
                loading: true
            }
        default:
            return state;
    }
}