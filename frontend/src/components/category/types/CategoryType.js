import React, { useContext, useState, useEffect } from "react";
import Category from "../Category";
import transactionContext from "../../../context/transaction/transactionContext";
import categoryContext from "../../../context/category/categoryContext";
import { useNavigate } from "react-router-dom";
import { FaExternalLinkAlt, FaChartPie } from "react-icons/fa";
import { getBudgetStatus } from "../../../utils/budgetColors";
import { ThemeContext } from "../../../context/theme/ThemeContext";

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
}) => {
   const { transactions } = useContext(transactionContext);
   const { categories } = useContext(categoryContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const [filteredCategories, setFilteredCategories] = useState([]);
   const [filteredTransactions, setFilteredTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [totalAmountBudgeted, setTotalAmountBudgeted] = useState(0);
   const [expectedSavings, setExpectedSavings] = useState(0);

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
   }, [filteredCategories]);

   useEffect(() => {
      if (transactions && filteredCategories.length > 0) {
         const filtered = transactions.filter((transaction) => {
            if (transaction.category && transaction.category.categoryId) {
               return filteredCategories.some(
                  (category) =>
                     category.categoryId === transaction.category.categoryId
               );
            }
            return false;
         });
         setFilteredTransactions(filtered);
      } else {
         setFilteredTransactions([]);
      }
   }, [transactions, filteredCategories]);

   useEffect(() => {
      const totalSpent = filteredTransactions.reduce(
         (sum, transaction) => sum + transaction.amount,
         0
      );
      setTotalAmountSpent(Math.round(totalSpent));
   }, [filteredTransactions]);

   useEffect(() => {
      const totalBudgeted = totalAmountBudgeted;
      const savedAmount = categoryType.savedAmount || 0;
      const calculatedSavings = savedAmount + (totalBudgeted - totalAmountSpent);
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
      </div>
   );
};

export default CategoryType;
