import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AlertContext from "../context/alert/alertContext";
import categoryTypeContext from "../context/categoryTypes/categoryTypeContext";
import CategoryTypeSlider from "../components/category/types/CategoryTypeSlider";

const CategoryTypeInputPage = () => {
   const navigate = useNavigate();
   const { setAlert } = useContext(AlertContext);
   const { error, addCategoryTypes, clearErrors, categoryTypes } =
      useContext(categoryTypeContext);
   const [categoryTypesInput, setCategoryTypesInput] = useState([
      { name: "Needs", budgetAllocationPercentage: 0.5 },
      { name: "Wants", budgetAllocationPercentage: 0.3 },
      { name: "Investments", budgetAllocationPercentage: 0.2 },
   ]);

   useEffect(() => {
      if (categoryTypes && categoryTypes.length > 0) {
         setAlert("Category types added successfully", "SUCCESS");
         navigate("/home");
      }
   }, [categoryTypes]);

   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error]);

   const handleSliderChange = (name, value) => {
      const updatedCategoryTypes = categoryTypesInput.map((cat) =>
         cat.name === name ? { ...cat, budgetAllocationPercentage: value } : cat
      );

      setCategoryTypesInput(updatedCategoryTypes);
   };

   const handleSubmit = () => {
      // Calculate the total percentage of all sliders
      const totalPercentage = categoryTypesInput.reduce(
         (sum, cat) => sum + cat.budgetAllocationPercentage,
         0
      );

      if (totalPercentage === 1) {
         addCategoryTypes(categoryTypesInput);
      } else {
         // Show an alert to the user if the total percentage is not 1
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
