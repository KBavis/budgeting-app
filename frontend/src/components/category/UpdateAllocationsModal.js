import React, { useContext, useState, useEffect } from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import categoryContext from "../../context/category/categoryContext";
import AlertContext from "../../context/alert/alertContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";

const UpdateAllocationsModal = ({ categoryType, onClose }) => {
   // Local State
   const [selectedCategories, setSelectedCategories] = useState([]);
   const [initialCategories, setInitialCategories] = useState([]);
   const [totalBudgetAllocation, setTotalBudgetAllocation] = useState(0);

   // Global State
   const { updateAllocations, categories } = useContext(categoryContext);
   const { fetchCategoryType } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);

   useEffect(() => {
      if (categoryType) {
         const filteredCategories = categories
            .filter(
               (cat) =>
                  cat.categoryType.categoryTypeId ===
                  categoryType.categoryTypeId
            )
            .map((cat) => ({
               ...cat,
               budgetAllocationPercentage: cat.budgetAllocationPercentage || 0,
            }));

         setSelectedCategories(filteredCategories);
         setInitialCategories(filteredCategories);
         calculateTotalBudgetAllocation(filteredCategories);
      }
   }, [categoryType, categories]);

   // Function to calculate the total budget allocation percentage
   const calculateTotalBudgetAllocation = (categories) => {
      const total = categories.reduce(
         (sum, category) => sum + category.budgetAllocationPercentage,
         0
      );
      setTotalBudgetAllocation(total);
   };

   // Function to handle submission of form
   const handleSubmit = async () => {
      // Create UpdateCategoryDtos for existing categories that were modified
      const updateCategoryDtos = selectedCategories
         .filter(
            (category, index) =>
               category.budgetAllocationPercentage !==
               initialCategories[index].budgetAllocationPercentage
         )
         .map((category) => ({
            categoryId: category.categoryId,
            budgetAllocationPercentage: category.budgetAllocationPercentage,
         }));

      if (!updateCategoryDtos || updateCategoryDtos.length === 0) {
         setAlert("No allocation updates made", "danger");
         return;
      }

      // Check if the total budget allocation percentage exceeds 1.0
      const roundedPercent = roundToNearestTenthPercent(totalBudgetAllocation);
      if (roundedPercent > 1.0) {
         setAlert(
            "Total budget allocation percentage cannot exceed 100%",
            "danger"
         );
         return;
      }

      // Call updateCategoryAllocations with the required parameters
      await updateAllocations(updateCategoryDtos, categoryType.categoryTypeId);
      await fetchCategoryType(categoryType.categoryTypeId);

      onClose();
   };

   const roundToNearestTenthPercent = (value) => {
      return Math.round(value * 1000) / 1000;
   };

   // Function to handle slider change for existing categories
   const handleSliderChange = (categoryId, value) => {
      const updatedCategories = selectedCategories.map((category) =>
         category.categoryId === categoryId
            ? { ...category, budgetAllocationPercentage: value }
            : category
      );
      setSelectedCategories(updatedCategories);
      calculateTotalBudgetAllocation(updatedCategories);
   };

   // Function to reset categories to initial percentages
   const handleReset = () => {
      setSelectedCategories(initialCategories);
      calculateTotalBudgetAllocation(initialCategories);
   };

   return (
      <div className="fixed inset-0 flex items-center justify-center z-40 backdrop-blur-sm overflow-y-auto">
         <div className="bg-white p-8 rounded shadow-lg w-1/2 flex flex-col justify-between">
            <div className="flex justify-center items-center mb-4">
               <div>
                  <h2 className="text-3xl font-extrabold text-indigo-600 text-center mb-2">
                     Update Allocations
                  </h2>
                  <p className="text-center">
                     Adjust the budget allocation for existing categories.
                  </p>
               </div>
            </div>
            {/* Display Total Budget Allocation */}
            {categoryType && (
               <div className="mb-4 p-4 border border-gray-300 rounded-md">
                  <p className="block text-sm font-bold text-gray-700">
                     Total Budget Allocation:{" "}
                     {(totalBudgetAllocation * 100).toFixed(1)}%
                  </p>
               </div>
            )}
            {/* Adjust Budget Allocation Section */}
            {categoryType && (
               <div
                  className="p-4 border border-gray-300 rounded-md"
                  style={{ maxHeight: "300px", overflowY: "auto" }}
               >
                  <div className="flex justify-between items-center mb-2">
                     <p className="block text-sm font-medium text-gray-700">
                        Adjust Existing Category Allocations
                     </p>
                     <button
                        onClick={handleReset}
                        className="px-3 py-1 text-sm text-white bg-blue-500 rounded hover:bg-blue-600"
                     >
                        Reset
                     </button>
                  </div>
                  {selectedCategories.map((category) => (
                     <div key={category.categoryId} className="mb-6">
                        <div className="flex justify-center items-center mt-3 mb-[2px]">
                           <label className="block font-bold mr-2">
                              {category.name}
                           </label>
                        </div>
                        <div className="relative mb-2 py-4 flex justify-between items-center">
                           <div className="text-gray-600 mr-2">
                              {(
                                 category.budgetAllocationPercentage * 100
                              ).toFixed(0)}
                              %
                           </div>
                           <Slider
                              className="w-3/4"
                              value={category.budgetAllocationPercentage || 0}
                              min={0}
                              max={1}
                              step={0.01}
                              onChange={(value) =>
                                 handleSliderChange(category.categoryId, value)
                              }
                              handleStyle={{
                                 borderColor: "rgb(79, 70, 229)",
                                 height: 20,
                                 width: 20,
                                 marginTop: -8,
                                 backgroundColor: "rgb(99, 102, 241)",
                              }}
                              railStyle={{
                                 backgroundColor: "rgb(156, 163, 175)",
                                 height: 6,
                              }}
                              trackStyle={{
                                 backgroundColor: "rgb(79, 70, 229)",
                                 height: 6,
                              }}
                              dotStyle={{ display: "none" }}
                              activeDotStyle={{ display: "none" }}
                           />
                        </div>
                     </div>
                  ))}
               </div>
            )}
            <div className="flex justify-between mt-4">
               <button
                  onClick={onClose}
                  className="modal-button-cancel px-4 py-2 text-white bg-red-500 rounded hover:bg-red-600"
               >
                  Close
               </button>
               <button
                  onClick={handleSubmit}
                  className="modal-button-submit px-4 py-2 text-white bg-green-500 rounded hover:bg-green-600"
               >
                  Submit
               </button>
            </div>
         </div>
      </div>
   );
};

export default UpdateAllocationsModal;
