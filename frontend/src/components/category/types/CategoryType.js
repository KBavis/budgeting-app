import React, { useContext, useState, useEffect } from "react";
import Category from "../Category";
import transactionContext from "../../../context/transaction/transactionContext";
import categoryContext from "../../../context/category/categoryContext";
import categoryTypeContext from "../../../context/category/types/categoryTypeContext";
import AlertContext from "../../../context/alert/alertContext";
import { useNavigate } from "react-router-dom";
import { FaExternalLinkAlt, FaChartPie, FaPen, FaCheck, FaTimes, FaSlidersH } from "react-icons/fa";
import { getBudgetStatus } from "../../../utils/budgetColors";
import { ThemeContext } from "../../../context/theme/ThemeContext";
import UpdateCategoryTypeAllocationsModal from "./UpdateCategoryTypeAllocationsModal";

/**
 * Enhanced CategoryType component with explicit theme logic ensuring
 * dark glassmorphic outer cards and high contrast text in Dark Mode.
 */
const CategoryType = ({
   categoryType,
   handleShowSplitTransactionModal,
   handleShowReduceTransactionModal,
   handleShowRenameTransactionModal,
   handleShowAssignCategoryModal,
   handleShowUpdateAllocationsModal,
   handleShowRenameCategoryModal,
   handleShowUpdateCategoryTypeAllocationsModal,
}) => {
   const { transactions } = useContext(transactionContext);
   const { categories } = useContext(categoryContext);
   const { updateCategoryType } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const [filteredCategories, setFilteredCategories] = useState([]);
   const [filteredTransactions, setFilteredTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [totalAmountBudgeted, setTotalAmountBudgeted] = useState(0);
   const [expectedSavings, setExpectedSavings] = useState(0);
   const [showUpdateCategoryTypeAllocationsModal, setShowUpdateCategoryTypeAllocationsModal] = useState(false);

   // Allocation percentage calculation & local state for editing
   const rawAllocationPct = categoryType?.budgetAllocationPercentage || 0;
   const allocationPct = Math.round(
      rawAllocationPct > 1 ? rawAllocationPct : rawAllocationPct * 100
   );
   const [isEditingAllocation, setIsEditingAllocation] = useState(false);
   const [editPct, setEditPct] = useState(allocationPct);

   useEffect(() => {
      setEditPct(allocationPct);
   }, [allocationPct]);

   const handleConfirmAllocation = async () => {
      const parsedPct = parseFloat(editPct);
      if (isNaN(parsedPct) || parsedPct < 0 || parsedPct > 100) {
         setAlert("Please enter a valid allocation percentage between 0 and 100", "danger");
         setEditPct(allocationPct);
         setIsEditingAllocation(false);
         return;
      }

      if (parsedPct !== allocationPct) {
         await updateCategoryType(categoryType.categoryTypeId, {
            budgetAllocationPercentage: parsedPct / 100.0,
         });
         setAlert(`Updated ${categoryType.name} allocation to ${parsedPct}%`, "success");
      }
      setIsEditingAllocation(false);
   };

   const handleCancelAllocation = () => {
      setEditPct(allocationPct);
      setIsEditingAllocation(false);
   };

   // Filter and deduplicate categories for this CategoryType
   useEffect(() => {
      const filtered = (categories || []).filter(
         (category) => category.categoryTypeId === categoryType.categoryTypeId
      );

      const map = new Map();
      filtered.forEach((cat) => {
         if (cat && cat.categoryId) {
            map.set(cat.categoryId, cat);
         }
      });

      setFilteredCategories(Array.from(map.values()));
   }, [categories, categoryType.categoryTypeId]);

   useEffect(() => {
      if (filteredCategories.length > 0) {
         const totalBudgeted = filteredCategories.reduce(
            (sum, category) => sum + category.budgetAmount,
            0
         );
         setTotalAmountBudgeted(Math.round(totalBudgeted));
      } else {
         setTotalAmountBudgeted(0);
      }

      if (transactions) {
         const categoryIds = new Set(
            filteredCategories.map((cat) => cat.categoryId)
         );
         const matchingTx = transactions.filter(
            (tx) => tx.category && categoryIds.has(tx.category.categoryId)
         );

         setFilteredTransactions(matchingTx);

         const sumSpent = matchingTx.reduce(
            (sum, tx) => sum + (tx.amount || 0),
            0
         );
         setTotalAmountSpent(Math.round(sumSpent));
      }
   }, [filteredCategories, transactions]);

   useEffect(() => {
      const savedAmount = categoryType.savedAmount || 0;
      const calculatedSavings = savedAmount + (totalAmountBudgeted - totalAmountSpent);
      setExpectedSavings(calculatedSavings);
   }, [totalAmountBudgeted, filteredCategories, filteredTransactions, categoryType.savedAmount, totalAmountSpent]);

   const navigate = useNavigate();

   const budgetStatus = getBudgetStatus(totalAmountSpent, totalAmountBudgeted);

   const handleClick = () => {
      navigate(`/category/type/${categoryType.name.toLowerCase()}`);
   };

   const progressPercentage = totalAmountBudgeted > 0
      ? Math.min(Math.round((totalAmountSpent / totalAmountBudgeted) * 100), 100)
      : 0;

   const triggerAllocationModal = () => {
      if (handleShowUpdateCategoryTypeAllocationsModal) {
         handleShowUpdateCategoryTypeAllocationsModal();
      } else {
         setShowUpdateCategoryTypeAllocationsModal(true);
      }
   };

   return (
      <div
         className={`relative backdrop-blur-md border rounded-2xl p-6 w-full flex flex-col min-h-[420px] max-h-[720px] transition-all duration-300 ${
            isDark
               ? "bg-slate-950/80 border-slate-800 text-slate-100 shadow-2xl hover:border-slate-700"
               : "bg-slate-200/70 border-slate-300/80 text-slate-800 shadow-lg hover:border-slate-400"
         }`}
      >
         {/* Sticky Card Header */}
         <div
            className={`sticky top-0 z-20 backdrop-blur-md p-4 sm:p-5 mb-4 border rounded-xl shadow-sm ${
               isDark
                  ? "bg-slate-900/95 border-slate-800"
                  : "bg-white/95 border-slate-200"
            }`}
         >
            <div className="flex justify-between items-center mb-3">
               <div className="flex items-center gap-2.5">
                  <div className="p-2 bg-brand-500/20 text-brand-400 rounded-xl border border-brand-500/30">
                     <FaChartPie className="w-4 h-4" />
                  </div>
                  <h3 className={`text-xl font-extrabold tracking-wide ${isDark ? "text-white" : "text-slate-900"}`}>
                     {categoryType.name}
                  </h3>
               </div>

               <div className="flex items-center gap-2">
                  {/* Allocation Percentage Badge / Slider Trigger Control */}
                  {isEditingAllocation ? (
                     <div className="flex items-center gap-1 bg-indigo-500/10 border border-indigo-500/40 px-2 py-0.5 rounded-xl">
                        <input
                           type="number"
                           min="0"
                           max="100"
                           step="1"
                           value={editPct}
                           onChange={(e) => setEditPct(e.target.value)}
                           autoFocus
                           onKeyDown={(e) => {
                              if (e.key === "Enter") handleConfirmAllocation();
                              if (e.key === "Escape") handleCancelAllocation();
                           }}
                           className={`w-12 px-1 py-0.5 text-xs font-bold text-center rounded-lg border focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                              isDark ? "bg-slate-900 border-indigo-500 text-white" : "bg-white border-indigo-500 text-slate-900"
                           }`}
                        />
                        <span className="text-xs font-extrabold text-indigo-400">%</span>
                        <button
                           type="button"
                           onClick={handleConfirmAllocation}
                           className="p-1 rounded-md bg-emerald-600 hover:bg-emerald-500 text-white transition-all shadow-sm ml-1"
                           title="Save Allocation"
                        >
                           <FaCheck size={9} />
                        </button>
                        <button
                           type="button"
                           onClick={handleCancelAllocation}
                           className={`p-1 rounded-md transition-all ${
                              isDark ? "bg-slate-800 text-slate-400 hover:text-white" : "bg-slate-200 text-slate-600 hover:text-slate-900"
                           }`}
                           title="Cancel"
                        >
                           <FaTimes size={9} />
                        </button>
                     </div>
                  ) : (
                     <button
                        type="button"
                        onClick={triggerAllocationModal}
                        className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-xl text-xs font-bold border transition-all cursor-pointer group ${
                           isDark
                              ? "bg-indigo-500/20 text-indigo-300 border-indigo-500/30 hover:bg-indigo-500/30 hover:border-indigo-400"
                              : "bg-indigo-50 text-indigo-700 border-indigo-200 hover:bg-indigo-100 hover:border-indigo-300 shadow-sm"
                        }`}
                        title="Click to adjust CategoryType income allocations with sliders"
                     >
                        <FaSlidersH size={10} className="text-indigo-400" />
                        <span>{allocationPct}% Allocated</span>
                        <FaPen size={9} className="opacity-60 group-hover:opacity-100 transition-opacity" />
                     </button>
                  )}

                  <button
                     type="button"
                     onClick={handleClick}
                     className={`p-2 rounded-xl transition-colors ${
                        isDark
                           ? "text-slate-400 hover:text-white hover:bg-slate-800"
                           : "text-slate-400 hover:text-slate-700 hover:bg-slate-100"
                     }`}
                     title="View detailed category type analysis"
                  >
                     <FaExternalLinkAlt size={14} />
                  </button>
               </div>
            </div>

            {/* Spent / Budgeted Display */}
            <div className="flex justify-between items-baseline mb-2">
               <span className={`text-xs font-semibold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                  Spent / Allocated
               </span>
               <span className={`text-sm font-bold ${isDark ? "text-slate-200" : "text-slate-700"}`}>
                  <span className={budgetStatus.textClass}>${totalAmountSpent}</span> / ${totalAmountBudgeted}
               </span>
            </div>

            {/* Status Pill Badge */}
            <div className="flex items-center justify-between gap-2 mb-3">
               <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-extrabold uppercase tracking-wider border ${budgetStatus.bg}/20 ${budgetStatus.text} border-${budgetStatus.color}/30 shadow-sm`}>
                  {budgetStatus.label}
               </span>
               <span className={`text-xs font-bold ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                  {progressPercentage}% Utilized
               </span>
            </div>

            {/* Glowing Progress Bar */}
            <div className={`w-full rounded-full h-2.5 overflow-hidden ${isDark ? "bg-slate-800" : "bg-slate-200"}`}>
               <div
                  className={`h-full rounded-full transition-all duration-500 ease-in-out ${budgetStatus.colorClass}`}
                  style={{ width: `${progressPercentage}%` }}
               />
            </div>
         </div>

         {/* Category List */}
         <div className="flex-1 overflow-y-auto pr-1 scrollbar-hide">
            <div className="flex flex-col gap-3">
               {filteredCategories.map((category) => (
                  <Category
                     key={category.categoryId}
                     category={category}
                     handleShowSplitTransactionModal={handleShowSplitTransactionModal}
                     handleShowReduceTransactionModal={handleShowReduceTransactionModal}
                     handleShowRenameTransactionModal={handleShowRenameTransactionModal}
                     handleShowAssignCategoryModal={handleShowAssignCategoryModal}
                     handleShowUpdateAllocationsModal={handleShowUpdateAllocationsModal}
                     handleShowRenameCategoryModal={handleShowRenameCategoryModal}
                  />
               ))}
            </div>
         </div>

         {showUpdateCategoryTypeAllocationsModal && (
            <UpdateCategoryTypeAllocationsModal
               onClose={() => setShowUpdateCategoryTypeAllocationsModal(false)}
            />
         )}
      </div>
   );
};

export default CategoryType;
