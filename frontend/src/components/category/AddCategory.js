import React, { useContext, useState } from "react";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import categoryContext from "../../context/category/categoryContext";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import AlertContext from "../../context/alert/alertContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";

const AddCategory = ({ onClose }) => {
   // Local State
   const [categoryName, setCategoryName] = useState("");
   const [percentage, setPercentage] = useState(0);
   const [selectedType, setSelectedType] = useState(null);
   const [selectedCategories, setSelectedCategories] = useState([]);
   const [initialCategories, setInitialCategories] = useState([]);
   const [totalBudgetAllocation, setTotalBudgetAllocation] = useState(0);

   // Global State
   const { categoryTypes, fetchCategoryType, fetchCategoryTypes } = useContext(categoryTypeContext);
   const { addCategory, fetchCategories } = useContext(categoryContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   // Function to handle category type selection
   const handleCategoryTypeSelect = (type) => {
      const initialCat = (type.categories || []).map((cat) => ({
         ...cat,
         budgetAllocationPercentage: cat.budgetAllocationPercentage || 0,
      }));

      // Deduplicate
      const map = new Map();
      initialCat.forEach((c) => {
         if (c && c.categoryId) map.set(c.categoryId, c);
      });

      const unique = Array.from(map.values());
      setSelectedType(type);
      setSelectedCategories(unique);
      setInitialCategories(unique);
      calculateTotalBudgetAllocation(unique, percentage);
   };

   // Function to calculate the total budget allocation percentage
   const calculateTotalBudgetAllocation = (
      categories,
      newCategoryPercentage
   ) => {
      const total =
         categories.reduce(
            (sum, category) => sum + (category.budgetAllocationPercentage || 0),
            0
         ) + newCategoryPercentage;
      setTotalBudgetAllocation(total);
   };

   const roundToNearestTenthPercent = (value) => {
      return Math.round(value * 1000) / 1000;
   };

   // Function to handle submission of form
   const handleSubmit = async () => {
      if (!selectedType) {
         setAlert("Please select a Category Type", "danger");
         return;
      }
      if (!categoryName) {
         setAlert("New category must have a name", "danger");
         return;
      }

      const updateCategoryDtos = selectedCategories
         .filter(
            (category, index) =>
               category.budgetAllocationPercentage !==
               (initialCategories[index] ? initialCategories[index].budgetAllocationPercentage : 0)
         )
         .map((category) => ({
            categoryId: category.categoryId,
            budgetAllocationPercentage: category.budgetAllocationPercentage,
         }));

      const newCategory = {
         name: categoryName,
         budgetAllocationPercentage: percentage,
         categoryTypeId: selectedType.categoryTypeId,
         budgetAmount: selectedType.budgetAmount * percentage,
      };

      const roundedPercent = roundToNearestTenthPercent(totalBudgetAllocation);
      if (roundedPercent > 1.0) {
         setAlert(
            "Total budget allocation percentage cannot exceed 100%",
            "danger"
         );
         return;
      }

      await addCategory(newCategory, updateCategoryDtos, selectedType);
      await fetchCategoryType(selectedType.categoryTypeId);
      if (fetchCategoryTypes) await fetchCategoryTypes();
      if (fetchCategories) await fetchCategories();

      onClose();
   };

   const handleNewCategorySliderChange = (value) => {
      setPercentage(value);
      calculateTotalBudgetAllocation(selectedCategories, value);
   };

   const handleSliderChange = (categoryId, value) => {
      const updatedCategories = selectedCategories.map((category) =>
         category.categoryId === categoryId
            ? { ...category, budgetAllocationPercentage: value }
            : category
      );
      setSelectedCategories(updatedCategories);
      calculateTotalBudgetAllocation(updatedCategories, percentage);
   };

   const handleReset = () => {
      setSelectedCategories(initialCategories);
      setPercentage(0);
      calculateTotalBudgetAllocation(initialCategories, 0);
   };

   return (
      <Modal isOpen={true} onClose={onClose} title="Add Category" size="lg">
         <div className="flex flex-col gap-4">
            <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               Select a Category Type, enter a name, and adjust budget allocation sliders for existing categories.
            </p>

            {/* Select Category Type Section */}
            <div className={`p-4 border rounded-xl flex flex-col gap-2 ${
               isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-slate-200"
            }`}>
               <label className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Select Category Type
               </label>
               <div className="flex flex-wrap gap-3">
                  {categoryTypes.map((type) => (
                     <label
                        key={type.categoryTypeId}
                        className={`flex items-center gap-2 px-3.5 py-2 rounded-xl border text-sm font-bold cursor-pointer transition-all ${
                           selectedType === type
                              ? "bg-brand-600 border-brand-500 text-white shadow-sm"
                              : isDark
                                 ? "bg-slate-900 border-slate-700 text-slate-300 hover:bg-slate-800"
                                 : "bg-white border-slate-300 text-slate-700 hover:bg-slate-100"
                        }`}
                     >
                        <input
                           type="radio"
                           name="categoryType"
                           value={type.name}
                           checked={selectedType === type}
                           onChange={() => handleCategoryTypeSelect(type)}
                           className="hidden"
                        />
                        {type.name}
                     </label>
                  ))}
               </div>
            </div>

            {/* Add Category Section */}
            {selectedType && (
               <div className={`p-4 border rounded-xl flex flex-col gap-3 ${
                  isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-slate-200"
               }`}>
                  <div className="flex flex-col gap-1">
                     <label htmlFor="categoryName" className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                        New Category Name
                     </label>
                     <input
                        type="text"
                        id="categoryName"
                        value={categoryName}
                        onChange={(e) => setCategoryName(e.target.value)}
                        placeholder="e.g. Dining Out"
                        className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all ${
                           isDark
                              ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                              : "bg-white border border-slate-300 text-slate-900 placeholder-slate-400"
                        }`}
                     />
                  </div>

                  <div className="flex flex-col gap-1">
                     <div className="flex justify-between items-center text-xs font-semibold">
                        <span className={isDark ? "text-slate-300" : "text-slate-700"}>New Category Allocation</span>
                        <span className="text-brand-600 dark:text-brand-400 font-extrabold">{(parseFloat(percentage) * 100).toFixed(0)}%</span>
                     </div>
                     <Slider
                        value={parseFloat(percentage) || 0}
                        min={0}
                        max={1}
                        step={0.01}
                        onChange={handleNewCategorySliderChange}
                        handleStyle={{
                           borderColor: "#6366F1",
                           backgroundColor: "#818CF8",
                        }}
                        trackStyle={{
                           backgroundColor: "#6366F1",
                        }}
                        railStyle={{
                           backgroundColor: isDark ? "#334155" : "#CBD5E1",
                        }}
                     />
                  </div>
               </div>
            )}

            {/* Display Total Budget Allocation */}
            {selectedType && (
               <div className={`p-3.5 border rounded-xl flex justify-between items-center ${
                  isDark ? "bg-slate-800/80 border-slate-700/80" : "bg-slate-100 border-slate-200"
               }`}>
                  <span className={`text-sm font-bold ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                     Total Budget Allocation:
                  </span>
                  <span className={`text-sm font-extrabold ${
                     totalBudgetAllocation > 1.0 ? 'text-red-500' : 'text-emerald-600 dark:text-emerald-400'
                  }`}>
                     {(totalBudgetAllocation * 100).toFixed(1)}%
                  </span>
               </div>
            )}

            {/* Adjust Budget Allocation Section */}
            {selectedType && (
               <div className={`p-4 border rounded-xl flex flex-col gap-3 max-h-[30vh] overflow-y-auto pr-1 ${
                  isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-slate-200"
               }`}>
                  <div className="flex justify-between items-center">
                     <span className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                        Adjust Existing Categories
                     </span>
                     <button
                        type="button"
                        onClick={handleReset}
                        className="text-xs font-semibold text-brand-600 dark:text-brand-400 hover:underline"
                     >
                        Reset Allocations
                     </button>
                  </div>
                  {selectedCategories.map((category) => (
                     <div key={category.categoryId} className="flex flex-col gap-1">
                        <div className="flex justify-between text-xs">
                           <span className={isDark ? "text-slate-300" : "text-slate-700 font-medium"}>{category.name}</span>
                           <span className={`font-bold ${isDark ? "text-slate-100" : "text-slate-900"}`}>
                              {((category.budgetAllocationPercentage || 0) * 100).toFixed(0)}%
                           </span>
                        </div>
                        <Slider
                           value={category.budgetAllocationPercentage || 0}
                           min={0}
                           max={1}
                           step={0.01}
                           onChange={(value) =>
                              handleSliderChange(category.categoryId, value)
                           }
                           handleStyle={{
                              borderColor: "#6366F1",
                              backgroundColor: "#818CF8",
                           }}
                           trackStyle={{
                              backgroundColor: "#6366F1",
                           }}
                           railStyle={{
                              backgroundColor: isDark ? "#334155" : "#CBD5E1",
                           }}
                        />
                     </div>
                  ))}
               </div>
            )}

            <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
               <button
                  type="button"
                  onClick={onClose}
                  className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors ${
                     isDark
                        ? "text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700"
                        : "text-slate-700 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 border border-slate-200"
                  }`}
               >
                  Cancel
               </button>
               <button
                  type="button"
                  onClick={handleSubmit}
                  className="px-5 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg transition-colors shadow-md"
               >
                  Submit
               </button>
            </div>
         </div>
      </Modal>
   );
};

export default AddCategory;
