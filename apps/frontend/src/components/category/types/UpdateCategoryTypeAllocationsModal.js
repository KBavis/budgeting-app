import React, { useContext, useState, useEffect } from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import categoryTypeContext from "../../../context/category/types/categoryTypeContext";
import IncomeContext from "../../../context/income/incomeContext";
import AlertContext from "../../../context/alert/alertContext";
import Modal from "../../layout/Modal";
import { ThemeContext } from "../../../context/theme/ThemeContext";
import { FaChartPie, FaCheckCircle, FaExclamationTriangle } from "react-icons/fa";

/**
 * UpdateCategoryTypeAllocationsModal
 * Allows tuning the budget allocation percentages across CategoryTypes (Needs, Wants, Investments)
 * using rc-slider components matching the Category allocation UI.
 */
const UpdateCategoryTypeAllocationsModal = ({ onClose }) => {
   const [selectedTypes, setSelectedTypes] = useState([]);
   const [initialTypes, setInitialTypes] = useState([]);
   const [totalAllocationPool, setTotalAllocationPool] = useState(0);

   const { categoryTypes, updateCategoryType, fetchCategoryTypes } = useContext(categoryTypeContext);
   const { incomes } = useContext(IncomeContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const totalUserIncome = (incomes || []).reduce(
      (sum, inc) => sum + (inc.amount || 0),
      0
   );

   useEffect(() => {
      if (categoryTypes && categoryTypes.length > 0) {
         const typesData = categoryTypes.map((ct) => ({
            categoryTypeId: ct.categoryTypeId,
            name: ct.name,
            budgetAllocationPercentage: ct.budgetAllocationPercentage || 0,
         }));

         setSelectedTypes(typesData);
         setInitialTypes(typesData);
         calculateTotalPool(typesData);
      }
   }, [categoryTypes]);

   const calculateTotalPool = (types) => {
      const sum = types.reduce(
         (acc, ct) => acc + (ct.budgetAllocationPercentage || 0),
         0
      );
      setTotalAllocationPool(sum);
   };

   const handleSliderChange = (categoryTypeId, value) => {
      const updated = selectedTypes.map((ct) =>
         ct.categoryTypeId === categoryTypeId
            ? { ...ct, budgetAllocationPercentage: value }
            : ct
      );
      setSelectedTypes(updated);
      calculateTotalPool(updated);
   };

   const handleReset = () => {
      setSelectedTypes(initialTypes);
      calculateTotalPool(initialTypes);
   };

   const handleSubmit = async () => {
      const roundedPool = Math.round(totalAllocationPool * 1000) / 1000;
      if (Math.abs(roundedPool - 1.0) > 0.001) {
         setAlert("Please ensure you are leveraging your entire income (CategoryType allocations must total exactly 100%)", "danger");
         return;
      }

      let changesMade = false;
      for (let i = 0; i < selectedTypes.length; i++) {
         const current = selectedTypes[i];
         const original = initialTypes.find(t => t.categoryTypeId === current.categoryTypeId);
         if (original && Math.abs(current.budgetAllocationPercentage - original.budgetAllocationPercentage) > 0.001) {
            changesMade = true;
            await updateCategoryType(current.categoryTypeId, {
               budgetAllocationPercentage: current.budgetAllocationPercentage,
            });
         }
      }

      if (!changesMade) {
         setAlert("No allocation changes made", "danger");
         return;
      }

      setAlert("CategoryType budget allocations updated successfully", "success");
      await fetchCategoryTypes();
      onClose();
   };

   const isNot100 = Math.abs(totalAllocationPool - 1.0) > 0.001;

   return (
      <Modal isOpen={true} onClose={onClose} title="Update CategoryType Allocations" size="lg">
         <div className="flex flex-col gap-5">
            <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               Adjust the income allocation sliders for your main CategoryTypes (Needs, Wants, Investments). Your total allocation must equal 100%.
            </p>

            {/* Total Budget Allocation Pool Summary Card */}
            <div className={`p-4 border rounded-2xl flex items-center justify-between shadow-sm transition-all ${
               isNot100
                  ? isDark
                     ? "bg-red-950/40 border-red-800 text-red-200"
                     : "bg-red-50 border-red-200 text-red-900"
                  : isDark
                     ? "bg-emerald-950/30 border-emerald-800/80 text-emerald-200"
                     : "bg-emerald-50 border-emerald-200 text-emerald-900"
            }`}>
               <div className="flex items-center gap-3">
                  <div className={`p-3 rounded-xl border ${
                     isNot100
                        ? "bg-red-500/20 text-red-500 border-red-500/30"
                        : "bg-emerald-500/20 text-emerald-400 border-emerald-500/30"
                  }`}>
                     {isNot100 ? <FaExclamationTriangle className="w-5 h-5" /> : <FaCheckCircle className="w-5 h-5" />}
                  </div>
                  <div className="text-left">
                     <p className={`text-xs font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        Income Pool Allocation
                     </p>
                     <p className="text-xs font-medium">
                        Total Income: <strong>${totalUserIncome.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 0 })}</strong>
                     </p>
                  </div>
               </div>

               <div className="text-right">
                  <span className={`text-2xl font-black ${
                     isNot100
                        ? "text-red-500"
                        : isDark ? "text-emerald-400" : "text-emerald-600"
                  }`}>
                     {(totalAllocationPool * 100).toFixed(1)}%
                  </span>
                  <p className={`text-xs font-semibold ${
                     isNot100
                        ? "text-red-400"
                        : isDark ? "text-emerald-400" : "text-emerald-600"
                  }`}>
                     {isNot100
                        ? totalAllocationPool < 1.0 ? "Must equal 100% of income" : "Exceeds 100% of income"
                        : "100% Fully Allocated"
                     }
                  </p>
               </div>
            </div>

            {/* Sliders Container */}
            <div className={`p-4 border rounded-2xl flex flex-col gap-3.5 max-h-[48vh] overflow-y-auto ${
               isDark
                  ? "bg-slate-900/60 border-slate-800"
                  : "bg-slate-50/80 border-slate-200"
            }`}>
               <div className="flex justify-between items-center pb-2 border-b border-slate-200 dark:border-slate-800">
                  <span className={`text-xs font-bold uppercase tracking-wider ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                     CategoryType Breakdown ({selectedTypes.length})
                  </span>
                  <button
                     type="button"
                     onClick={handleReset}
                     className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline"
                  >
                     Reset Allocations
                  </button>
               </div>

               {selectedTypes.map((type) => {
                  const dollarAmount = Math.round((type.budgetAllocationPercentage || 0) * totalUserIncome);
                  const percentDisplay = ((type.budgetAllocationPercentage || 0) * 100).toFixed(0);

                  return (
                     <div
                        key={type.categoryTypeId}
                        className={`p-4 border rounded-xl flex flex-col gap-2.5 transition-all ${
                           isDark
                              ? "bg-slate-800/90 border-slate-700/80 shadow-md"
                              : "bg-white border-slate-200 shadow-sm hover:shadow-md"
                        }`}
                     >
                        <div className="flex justify-between items-center">
                           <span className={`text-sm font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
                              {type.name}
                           </span>

                           <div className="flex items-center gap-2">
                              <span className="text-sm font-black text-indigo-600 dark:text-indigo-400">
                                 ${dollarAmount}
                              </span>
                              <span className={`text-xs font-bold px-2.5 py-0.5 rounded-full border ${
                                 isDark
                                    ? "bg-slate-700 text-slate-200 border-slate-600"
                                    : "bg-slate-100 text-slate-800 border-slate-200"
                              }`}>
                                 {percentDisplay}%
                              </span>
                           </div>
                        </div>

                        <Slider
                           value={type.budgetAllocationPercentage || 0}
                           min={0}
                           max={1}
                           step={0.01}
                           onChange={(value) =>
                              handleSliderChange(type.categoryTypeId, value)
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
                  className="px-5 py-2 text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-500 rounded-lg transition-colors shadow-md"
               >
                  Save Allocations
               </button>
            </div>
         </div>
      </Modal>
   );
};

export default UpdateCategoryTypeAllocationsModal;
