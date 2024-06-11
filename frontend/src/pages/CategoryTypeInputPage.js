import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AlertContext from "../context/alert/alertContext";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryTypeSlider from "../components/category/types/CategoryTypeSlider";
import authContext from "../context/auth/authContext";
import IncomeContext from "../context/income/incomeContext";
import accountContext from "../context/account/accountContext";

/**
 *  Page for adjusting our allocated budget for CategoryType
 */
const CategoryTypeInputPage = () => {
   //Local State
   const [categoryTypesInput, setCategoryTypesInput] = useState([
      { name: "Needs", budgetAllocationPercentage: 0.5 },
      { name: "Wants", budgetAllocationPercentage: 0.3 },
      { name: "Investments", budgetAllocationPercentage: 0.2 },
   ]);

   //Global State
   const { setAlert } = useContext(AlertContext);
   const { error, addCategoryTypes, clearErrors, categoryTypes } =
      useContext(categoryTypeContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { incomes, fetchIncomes } = useContext(IncomeContext);
   const { accounts, fetchAccounts } = useContext(accountContext);

   //React Hooks

   const navigate = useNavigate();

   //Alert user of failure
   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error]);

   //Fetch Needed Information on Refresh
   useEffect(() => {
      //Fetch Authenticated User
      if (!user && localStorage.token) {
         fetchAuthenticatedUser();
      }

      //Fetch Added Accounts
      if (!accounts || accounts.length == 0) {
         fetchAccounts();
      }

      //Fetch Incomes
      if (!incomes || incomes.length == 0) {
         fetchIncomes();
      }
   }, []);

   //Function to handle user updating their bduget allocation
   const handleSliderChange = (name, value) => {
      const updatedCategoryTypes = categoryTypesInput.map((cat) =>
         cat.name === name ? { ...cat, budgetAllocationPercentage: value } : cat
      );

      setCategoryTypesInput(updatedCategoryTypes);
   };

   //Function to handle a user attmepting to submit their adjusted allocations
   const handleSubmit = () => {
      const totalPercentage = categoryTypesInput.reduce(
         (sum, cat) => sum + cat.budgetAllocationPercentage,
         0
      );

      //Ensures user has utilized 100% of potential budget
      if (totalPercentage === 1) {
         addCategoryTypes(categoryTypesInput);
         setAlert("Category types added successfully", "success");
         navigate("/category/needs");
      } else {
         setAlert("Total percentage must equal 100%", "danger");
      }
   };

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center">
         <div className="max-w-md w-full text-center">
            <h1 className="text-4xl font-bold mb-8 text-white">
               Set Category Type Allocations
            </h1>
            <div className="flex flex-col items-center">
               {categoryTypesInput.map((categoryType) => (
                  <CategoryTypeSlider
                     key={categoryType.name}
                     categoryType={categoryType}
                     onSliderChange={handleSliderChange}
                  />
               ))}
               <button
                  onClick={handleSubmit}
                  className="font-bold py-3 px-6 rounded text-white bg-indigo-600 hover:bg-indigo-700 transition-colors duration-300"
               >
                  Submit
               </button>
            </div>
         </div>
      </div>
   );
};

export default CategoryTypeInputPage;
