import { useContext, useReducer } from "react";
import { FETCH_CATEGORY_PERFORMANCES_SUCCESS, FETCH_CATEGORY_PERFORMANCES_ERROR, SET_LOADING, CLEAR_ERRORS } from "./types";
import categoryPerformanceReducer from "./categoryPerformanceReducer";
import initialState from "../initialState";
import apiUrl from "../../../utils/url";
import axios from "axios";
import setAuthToken from "../../../utils/setAuthToken";
import CategoryPerformanceContext from "./categoryPerformanceContext";
import AlertContext from "../../alert/alertContext";

/**
 * Global state for Category Performances
 * 
 * @param  props 
 *      - props from App.js
 */
const CategoryPerformanceState = (props) => {

    const { setAlert } = useContext(AlertContext)
    const [state, dispatch] = useReducer(categoryPerformanceReducer, initialState)

    const fetchCategoryPerformances = async (categoryTypeId, monthYear) => {
        if (localStorage.token) {
            setAuthToken(localStorage.token);
        }

        console.log(monthYear)

        const config = {
            headers: {
                "Content-Type": "application/json",
            },
        };

        try {
            const res = await axios.get(
                `${apiUrl}/category/performance/${categoryTypeId}`,
                monthYear,
                config
            );
            dispatch({
                type: FETCH_CATEGORY_PERFORMANCES_SUCCESS,
                payload: res.data
            })
            setAlert("Successfully retrieved Category Performances", "success")
        } catch (err) {
            console.error(err);
            setAlert("Failed to retrieve Category Performances", "danger")
            dispatch({
                type: FETCH_CATEGORY_PERFORMANCES_ERROR,
                payload: err.response.data.error
            })
        }
    }

    /**
     * Clear any errors out of context 
     */
    const clearErrors = () => dispatch({ type: CLEAR_ERRORS });

    /**
     *  Functionality to set loading to true
     */
    const setLoading = () => dispatch({ type: SET_LOADING });


    return (
        <CategoryPerformanceContext.Provider value={{
            category_performances: state.category_performances,
            loading: state.loading,
            error: state.error,
            fetchCategoryPerformances,
            setLoading,
            clearErrors
        }}>
            {props.children}
        </CategoryPerformanceContext.Provider>
    )
}

export default CategoryPerformanceState;