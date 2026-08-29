import React, { useContext, useState, useEffect } from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import categoryContext from "../../context/category/categoryContext";
import AlertContext from "../../context/alert/alertContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";
import { FaChartPie, FaCheckCircle, FaExclamationTriangle } from "react-icons/fa";

/**
 * Premium UpdateAllocationsModal with category deduplication,
 * live dollar and percentage pool feedback, and dual-mode Light/Dark styling.
 */
const UpdateAllocationsModal = ({ categoryType, onClose }) => {
   // Local State
   const [selectedCategories, setSelectedCategories] = useState([]);
   const [initialCategories, setInitialCategories] = useState([]);
   const [totalBudgetAllocation, setTotalBudgetAllocation] = useState(0);

   // Global State
   const { updateAllocations, categories } = useContext(categoryContext);
   const { fetchCategoryType } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   useEffect(() => {
      if (categoryType && categories) {
         // Filter and deduplicate categories corresponding to this CategoryType
         const filtered = categories
            .filter(
               (cat) => cat.categoryTypeId === categoryType.categoryTypeId
            )
            .map((cat) => ({
               ...cat,
               budgetAllocationPercentage: cat.budgetAllocationPercentage || 0,
            }));

         const map = new Map();
         filtered.forEach((cat) => {
            if (cat && cat.categoryId) {
               map.set(cat.categoryId, cat);
            }
         });

         const uniqueCategories = Array.from(map.values());
         setSelectedCategories(uniqueCategories);
         setInitialCategories(uniqueCategories);
         calculateTotalBudgetAllocation(uniqueCategories);
      }
   }, [categoryType, categories]);

   const calculateTotalBudgetAllocation = (cats) => {
      const total = cats.reduce(
         (sum, category) => sum + (category.budgetAllocationPercentage || 0),
         0
      );
      setTotalBudgetAllocation(total);
   };

   const roundToNearestTenthPercent = (value) => {
      return Math.round(value * 1000) / 1000;
   };

   const handleSubmit = async () => {
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

      if (!updateCategoryDtos || updateCategoryDtos.length === 0) {
         setAlert("No allocation updates made", "danger");
         return;
      }

      const roundedPercent = roundToNearestTenthPercent(totalBudgetAllocation);
      if (roundedPercent > 1.0) {
         setAlert(
            "Total budget allocation percentage cannot exceed 100%",
            "danger"
         );
         return;
      }

      await updateAllocations(updateCategoryDtos, categoryType.categoryTypeId);
      await fetchCategoryType(categoryType.categoryTypeId);

      onClose();
   };

   const handleSliderChange = (categoryId, value) => {
      const updatedCategories = selectedCategories.map((category) =>
         category.categoryId === categoryId
            ? { ...category, budgetAllocationPercentage: value }
            : category
      );
      setSelectedCategories(updatedCategories);
      calculateTotalBudgetAllocation(updatedCategories);
   };

   const handleReset = () => {
      setSelectedCategories(initialCategories);
      calculateTotalBudgetAllocation(initialCategories);
   };

   const totalCategoryTypeAmount = categoryType ? categoryType.amountAllocated || categoryType.budgetAmount || 0 : 0;
   const isOver100 = totalBudgetAllocation > 1.0;

   return (
      <Modal isOpen={true} onClose={onClose} title="Update Category Allocations" size="lg">
         <div className="flex flex-col gap-5">
            <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               Fine-tune the budget allocation sliders for categories inside <strong>{categoryType?.name}</strong>.
            </p>

            {/* Total Budget Allocation Pool Summary Card */}
            {categoryType && (
               <div className={`p-4 border rounded-2xl flex items-center justify-between shadow-sm transition-all ${
                  isOver100
                     ? isDark
                        ? "bg-red-950/40 border-red-800 text-red-200"
                        : "bg-red-50 border-red-200 text-red-900"
                     : isDark
                        ? "bg-slate-800/80 border-slate-700/80 text-white"
                        : "bg-slate-50 border-slate-200 text-slate-900"
               }`}>
                  <div className="flex items-center gap-3">
                     <div className={`p-3 rounded-xl border ${
                        isOver100
                           ? "bg-red-500/20 text-red-500 border-red-500/30"
                           : "bg-emerald-500/20 text-emerald-500 border-emerald-500/30"
                     }`}>
                        {isOver100 ? <FaExclamationTriangle className="w-5 h-5" /> : <FaCheckCircle className="w-5 h-5" />}
                     </div>
                     <div className="text-left">
                        <p className={`text-xs font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                           Allocation Status
                        </p>
                        <p className="text-xs font-medium">
                           Pool Target: <strong>${totalCategoryTypeAmount.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 0 })}</strong>
                        </p>
                     </div>
                  </div>

                  <div className="text-right">
                     <span className={`text-2xl font-black ${
                        isOver100
                           ? "text-red-500"
                           : isDark ? "text-emerald-400" : "text-emerald-600"
                     }`}>
                        {(totalBudgetAllocation * 100).toFixed(1)}%
                     </span>
                     <p className={`text-xs font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        ${Math.round(totalBudgetAllocation * totalCategoryTypeAmount)} allocated
                     </p>
                  </div>
               </div>
            )}

            {/* Sliders Container */}
            {categoryType && (
               <div className={`p-4 border rounded-2xl flex flex-col gap-3.5 max-h-[48vh] overflow-y-auto ${
                  isDark
                     ? "bg-slate-900/60 border-slate-800"
                     : "bg-slate-50/80 border-slate-200"
               }`}>
                  <div className="flex justify-between items-center pb-2 border-b border-slate-200 dark:border-slate-800">
                     <span className={`text-xs font-bold uppercase tracking-wider ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                        Category Breakdown ({selectedCategories.length})
                     </span>
                     <button
                        type="button"
                        onClick={handleReset}
                        className="text-xs font-bold text-brand-600 dark:text-brand-400 hover:underline"
                     >
                        Reset Allocations
                     </button>
                  </div>

                  {selectedCategories.map((category) => {
                     const catDollarAmount = Math.round((category.budgetAllocationPercentage || 0) * totalCategoryTypeAmount);
                     const catPercent = ((category.budgetAllocationPercentage || 0) * 100).toFixed(0);

                     return (
                        <div
                           key={category.categoryId}
                           className={`p-4 border rounded-xl flex flex-col gap-2.5 transition-all ${
                              isDark
                                 ? "bg-slate-800/90 border-slate-700/80 shadow-md"
                                 : "bg-white border-slate-200 shadow-sm hover:shadow-md"
                           }`}
                        >
                           <div className="flex justify-between items-center">
                              <span className={`text-sm font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
                                 {category.name}
                              </span>

                              <div className="flex items-center gap-2">
                                 <span className="text-sm font-black text-brand-600 dark:text-brand-400">
                                    ${catDollarAmount}
                                 </span>
                                 <span className={`text-xs font-bold px-2.5 py-0.5 rounded-full border ${
                                    isDark
                                       ? "bg-slate-700 text-slate-200 border-slate-600"
                                       : "bg-slate-100 text-slate-800 border-slate-200"
                                 }`}>
                                    {catPercent}%
                                 </span>
                              </div>
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
                                 opacity: 1,
                                 width: 18,
                                 height: 18,
                                 marginTop: -6,
                                 boxShadow: "0 0 8px rgba(99, 102, 241, 0.5)",
                              }}
                              trackStyle={{
                                 backgroundColor: "#6366F1",
                                 height: 6,
                                 borderRadius: 3,
                              }}
                              railStyle={{
                                 backgroundColor: isDark ? "#1E293B" : "#E2E8F0",
                                 height: 6,
                                 borderRadius: 3,
                              }}
                           />
                        </div>
                     );
                  })}
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
                  Save Allocations
               </button>
            </div>
         </div>
      </Modal>
   );
};

export default UpdateAllocationsModal;
