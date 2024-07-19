import React, { useContext, useState } from "react";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import categoryContext from "../../context/category/categoryContext";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import AlertContext from "../../context/alert/alertContext";

const AddCategory = ({ onClose }) => {
   // Local State
   const [categoryName, setCategoryName] = useState("");
   const [percentage, setPercentage] = useState(0);
   const [selectedType, setSelectedType] = useState(null);
   const [selectedCategories, setSelectedCategories] = useState([]);
   const [initialCategories, setInitialCategories] = useState([]);

   // Global State
   const { categoryTypes, fetchCategoryType } = useContext(categoryTypeContext);
   const { addCategory } = useContext(categoryContext);
   const { setAlert } = useContext(AlertContext);

   // Function to close the modal
   const handleClose = () => {
      onClose();
   };

   // Function to handle category type selection
   const handleCategoryTypeSelect = (type) => {
      const initialCat = type.categories.map((cat) => ({
         ...cat,
         budgetAllocationPercentage: cat.budgetAllocationPercentage || 0,
      }));
      setSelectedType(type);
      setSelectedCategories(initialCat);
      setInitialCategories(initialCat);
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

      // Create CategoryDto for the new category
      if (!categoryName) {
         setAlert("New category must have a name", "danger");
         return;
      }
      const newCategory = {
         name: categoryName,
         budgetAllocationPercentage: percentage,
         categoryTypeId: selectedType.categoryTypeId,
         budgetAmount: selectedType.budgetAmount * percentage,
      };

      // Calculate the total budget allocation percentage
      const totalBudgetAllocation =
         selectedCategories.reduce(
            (total, category) => total + category.budgetAllocationPercentage,
            0
         ) + percentage;
      const roundedPercent = roundToNearestTenthPercent(totalBudgetAllocation);

      // Check if the total budget allocation percentage exceeds 1.0
      if (roundedPercent > 1.0) {
         setAlert(
            "Total budget allocation percentage cannot exceed 100%",
            "danger"
         );
         return;
      }

      // Call createCategory with the required parameters
      await addCategory(newCategory, updateCategoryDtos, selectedType);
      await fetchCategoryType(selectedType.categoryTypeId);

      onClose();
   };

   const roundToNearestTenthPercent = (value) => {
      return Math.round(value * 1000) / 1000;
   };

   // Function to handle slider change for new category percentage
   const handleNewCategorySliderChange = (value) => {
      setPercentage(value);
   };

   // Function to handle slider change for existing categories
   const handleSliderChange = (categoryId, value) => {
      setSelectedCategories((prevCategories) =>
         prevCategories.map((category) =>
            category.categoryId === categoryId
               ? { ...category, budgetAllocationPercentage: value }
               : category
         )
      );
   };

   // Function to reset categories to initial percentages
   const handleReset = () => {
      setSelectedCategories(initialCategories);
   };

   return (
      <div className="fixed inset-0 flex items-center justify-center z-40 backdrop-blur-sm overflow-y-auto">
         <div className="bg-white p-8 rounded shadow-lg w-1/2 flex flex-col justify-between">
            <div className="flex justify-center items-center mb-4">
               <div>
                  <h2 className="text-3xl font-extrabold text-indigo-600 text-center mb-2">
                     Add Category
                  </h2>
                  <p className="text-center">
                     Please select a Category Type, add a new category, and
                     adjust the budget allocation for existing categories.
                  </p>
               </div>
            </div>
            {/* Select Category Type Section */}
            <div className="mb-4">
               <p className="block text-sm font-medium text-gray-700 mb-2">
                  Select Category Type
               </p>
               <div className="flex mb-2">
                  {categoryTypes.map((type) => (
                     <div
                        key={type.categoryTypeId}
                        className="flex items-center mr-4"
                     >
                        <input
                           type="checkbox"
                           id={`categoryType_${type.id}`}
                           value={type.name}
                           checked={selectedType === type}
                           onChange={() => handleCategoryTypeSelect(type)}
                           className="mr-2"
                        />
                        <label
                           htmlFor={`categoryType_${type.id}`}
                           className="text-sm text-gray-700"
                        >
                           {type.name}
                        </label>
                     </div>
                  ))}
               </div>
            </div>
            {/* Add Category Section */}
            {selectedType && (
               <div className="mb-4 p-4 border border-gray-300 rounded-md">
                  <label
                     htmlFor="categoryName"
                     className="block text-sm font-medium text-gray-700"
                  >
                     New Category Name
                  </label>
                  <input
                     type="text"
                     id="categoryName"
                     value={categoryName}
                     onChange={(e) => setCategoryName(e.target.value)}
                     className="mt-1 p-2 w-1/2 border border-gray-300 rounded-md"
                  />
                  <div className="relative mb-2 py-4 flex justify-between items-center">
                     <div className="text-gray-600 mr-2">
                        {(parseFloat(percentage) * 100).toFixed(0)}%
                     </div>
                     <Slider
                        className="w-3/4"
                        value={parseFloat(percentage) || 0}
                        min={0}
                        max={1}
                        step={0.01}
                        onChange={handleNewCategorySliderChange}
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
            )}
            {/* Adjust Budget Allocation Section */}
            {selectedType && (
               <div className="p-4 border border-gray-300 rounded-md">
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
                  onClick={handleClose}
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

export default AddCategory;
