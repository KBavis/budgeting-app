import React, { useContext, useState, useEffect } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import CategoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import SummaryContext from "../../context/summary/summaryContext";
import AlertContext from "../../context/alert/alertContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";
import { FaCheck } from "react-icons/fa";

const AddTransaction = ({ onClose }) => {
   // Local State
   const [transactionName, setTransactionName] = useState("");
   const [transactionAmount, setTransactionAmount] = useState("");
   const [transactionDate, setTransactionDate] = useState("");
   const [selectedCategoryTypeId, setSelectedCategoryTypeId] = useState(null);
   const [selectedCategoryId, setSelectedCategoryId] = useState(null);

   // Global State
   const { addTransaction } = useContext(transactionContext);
   const { categories } = useContext(CategoryContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const { recalculateBudgetSummary, fetchBudgetSummaries } = useContext(SummaryContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   // Initialize selected category type if available
   useEffect(() => {
      if (categoryTypes && categoryTypes.length > 0 && !selectedCategoryTypeId) {
         setSelectedCategoryTypeId(categoryTypes[0].categoryTypeId);
      }
   }, [categoryTypes, selectedCategoryTypeId]);

   // Compute local today's date in YYYY-MM-DD
   const getLocalTodayStr = () => {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
   };
   const todayStr = getLocalTodayStr();

   const isFutureDate = Boolean(transactionDate && transactionDate > todayStr);

   // Check if transaction is for a previous month
   const isPreviousMonth = (() => {
      if (!transactionDate) return false;
      const txDate = new Date(transactionDate + "T00:00:00");
      const now = new Date();
      const firstOfCurrentMonth = new Date(now.getFullYear(), now.getMonth(), 1);
      return txDate < firstOfCurrentMonth;
   })();

   // Validate all required fields (Name, Amount > 0, Category selected, Valid Date selected)
   const isFormValid = Boolean(
      transactionName.trim() &&
      transactionAmount &&
      parseFloat(transactionAmount) > 0 &&
      selectedCategoryId &&
      transactionDate &&
      !isFutureDate
   );

   // Get categories for selected category type
   const activeCategories = (() => {
      if (!selectedCategoryTypeId) return [];
      const ct = (categoryTypes || []).find(c => c.categoryTypeId === selectedCategoryTypeId);
      if (!ct) return [];

      return (categories || []).filter(
         (c) => c.name !== "Miscellaneous" && c.categoryType && (c.categoryType.categoryTypeId === ct.categoryTypeId || c.categoryType.name === ct.name)
      );
   })();

   const handleSubmit = async (e) => {
      e.preventDefault();
      if (!isFormValid) {
         setAlert("Please fill in all fields including a category and valid date.", "danger");
         return;
      }

      const transactionDto = {
         updatedName: transactionName,
         updatedAmount: parseFloat(transactionAmount),
         date: transactionDate,
         categoryId: parseInt(selectedCategoryId, 10),
      };

      await addTransaction(transactionDto);

      // Recalculate budget summary if adding transaction to a previous month
      if (isPreviousMonth && transactionDate) {
         const txDate = new Date(transactionDate + "T00:00:00");
         const month = txDate.getMonth() + 1;
         const year = txDate.getFullYear();
         await recalculateBudgetSummary(month, year);
      }

      onClose();
   };

   return (
      <Modal isOpen={true} onClose={onClose} title="Add Transaction" size="md">
         <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               Create and categorize a custom transaction independent of financial institution sync.
            </p>

            {/* Name Input */}
            <div className="flex flex-col gap-1">
               <label htmlFor="transactionName" className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Transaction Name
               </label>
               <input
                  type="text"
                  id="transactionName"
                  value={transactionName}
                  onChange={(e) => setTransactionName(e.target.value)}
                  placeholder="e.g. Coffee shop"
                  className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all ${
                     isDark
                        ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                        : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400 focus:bg-white"
                  }`}
                  required
               />
            </div>

            {/* Amount Input */}
            <div className="flex flex-col gap-1">
               <label htmlFor="transactionAmount" className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Transaction Amount
               </label>
               <div className="relative flex items-center">
                  <span className={`absolute left-3 text-sm font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>$</span>
                  <input
                     type="number"
                     step="0.01"
                     id="transactionAmount"
                     value={transactionAmount}
                     onChange={(e) => setTransactionAmount(e.target.value)}
                     placeholder="0.00"
                     className={`w-full pl-7 pr-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all ${
                        isDark
                           ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                           : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400 focus:bg-white"
                     }`}
                     required
                  />
               </div>
            </div>

            {/* Category Type & Category Selection (Pills Pattern) */}
            <div className="flex flex-col gap-2">
               <label className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Select Category
               </label>

               {/* Category Type Tabs */}
               <div className="flex items-center gap-2 overflow-x-auto no-scrollbar pb-1">
                  {(categoryTypes || []).map((ct) => {
                     const isSelected = selectedCategoryTypeId === ct.categoryTypeId;
                     return (
                        <button
                           key={ct.categoryTypeId}
                           type="button"
                           onClick={() => {
                              setSelectedCategoryTypeId(ct.categoryTypeId);
                              setSelectedCategoryId(null);
                           }}
                           className={`px-3 py-1.5 rounded-xl text-xs font-bold whitespace-nowrap transition-all border ${
                              isSelected
                                 ? 'bg-indigo-600 border-indigo-500 text-white shadow-md'
                                 : isDark
                                 ? 'bg-slate-800/80 border-slate-700/70 text-slate-300 hover:bg-slate-700 hover:text-white'
                                 : 'bg-slate-100 border-slate-300 text-slate-700 hover:bg-slate-200'
                           }`}
                        >
                           {ct.name}
                        </button>
                     );
                  })}
               </div>

               {/* Category Pills Grid */}
               <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 max-h-[180px] overflow-y-auto no-scrollbar p-1 border rounded-xl border-slate-700/30">
                  {activeCategories.map((cat) => {
                     const isCatSelected = selectedCategoryId === cat.categoryId;
                     return (
                        <button
                           key={cat.categoryId}
                           type="button"
                           onClick={() => setSelectedCategoryId(cat.categoryId)}
                           className={`p-2.5 rounded-xl text-xs font-bold text-left transition-all border flex items-center justify-between gap-1.5 ${
                              isCatSelected
                                 ? 'bg-indigo-600 border-indigo-400 text-white shadow-md ring-2 ring-indigo-400/50'
                                 : isDark
                                 ? 'bg-slate-800/60 border-slate-700/60 text-slate-200 hover:bg-indigo-600/60 hover:border-indigo-500 hover:text-white'
                                 : 'bg-white border-slate-200 text-slate-800 hover:bg-indigo-50 hover:border-indigo-300'
                           }`}
                        >
                           <span className="truncate">{cat.name}</span>
                           {isCatSelected && <FaCheck className="w-3 h-3 text-white shrink-0" />}
                        </button>
                     );
                  })}
               </div>
            </div>

            {/* Date Input */}
            <div className="flex flex-col gap-1">
               <label htmlFor="transactionDate" className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Transaction Date
               </label>
               <input
                  type="date"
                  id="transactionDate"
                  value={transactionDate}
                  max={todayStr}
                  style={{ colorScheme: isDark ? "dark" : "light" }}
                  onChange={(e) => setTransactionDate(e.target.value)}
                  className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 transition-all ${
                     isFutureDate
                        ? "bg-red-500/10 border-2 border-red-500 text-red-500 focus:ring-red-500/50"
                        : isDark
                           ? "bg-slate-900 border border-slate-700 text-slate-100 focus:ring-indigo-500/50"
                           : "bg-slate-50 border border-slate-300 text-slate-900 focus:bg-white focus:ring-indigo-500/50"
                  }`}
                  required
               />
               {isFutureDate ? (
                  <p className="text-xs font-bold text-red-500 mt-1 flex items-center gap-1">
                     ⚠️ Date cannot be in the future. Please select today ({todayStr}) or an earlier date.
                  </p>
               ) : (
                  <p className={`text-[10px] ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                     Date must be on or before today ({todayStr})
                  </p>
               )}
            </div>

            {/* Actions */}
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
                  type="submit"
                  disabled={!isFormValid}
                  className={`px-5 py-2 text-sm font-bold rounded-lg transition-all shadow-md ${
                     isFormValid
                        ? 'bg-brand-600 hover:bg-brand-500 text-white cursor-pointer hover:scale-105 active:scale-95'
                        : 'bg-slate-700/50 text-slate-500 border border-slate-700/40 cursor-not-allowed'
                  }`}
               >
                  Confirm
               </button>
            </div>
         </form>
      </Modal>
   );
};

export default AddTransaction;