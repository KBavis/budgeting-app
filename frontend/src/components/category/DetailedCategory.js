import React, { useState, useEffect, useContext } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import DetailedCategoryTransaction from "../transaction/DetailedCategoryTransaction";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import categoryContext from "../../context/category/categoryContext";
import AlertContext from "../../context/alert/alertContext";
import ConfirmationModal from "../layout/ConfirmationModal";
import { ThemeContext } from "../../context/theme/ThemeContext";
import { getBudgetStatus } from "../../utils/budgetColors";
import { FaTrashAlt, FaPen, FaSlidersH, FaSearch, FaTimes } from "react-icons/fa";

const DetailedCategory = ({
   category,
   handleShowSplitTransactionModal,
   handleShowReduceTransactionModal,
   handleShowRenameTransactionModal,
   handleShowAssignCategoryModal,
   handleShowUpdateAllocationsModal,
   handleShowRenameCategoryModal
}) => {
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [budgetUsage, setBudgetUsage] = useState(0);
   const [filteredTransactions, setFilteredTransactions] = useState([]);
   
   // Dropdown Filter Menu State
   const [showFilterDropdown, setShowFilterDropdown] = useState(false);

   // Transaction Filter Criteria
   const [nameQuery, setNameQuery] = useState("");
   const [amountMode, setAmountMode] = useState("range"); // "range" or "exact"
   const [exactAmount, setExactAmount] = useState("");
   const [minAmount, setMinAmount] = useState("");
   const [maxAmount, setMaxAmount] = useState("");

   const [dateMode, setDateMode] = useState("range"); // "range" or "exact"
   const [exactDate, setExactDate] = useState("");
   const [startDate, setStartDate] = useState("");
   const [endDate, setEndDate] = useState("");

   const [showConfirmDelete, setShowConfirmDelete] = useState(false);

   // Global State
   const { transactions, removeCategory } = useContext(transactionContext);
   const { deleteCategory } = useContext(categoryContext);
   const { fetchCategoryType } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   useEffect(() => {
      if (transactions) {
         const filtered = transactions.filter(
            (transaction) =>
               transaction.category &&
               transaction.category.categoryId === category.categoryId
         );

         setFilteredTransactions(filtered);

         const sum = filtered.reduce((acc, transaction) => {
            return acc + transaction.amount;
         }, 0);

         setTotalAmountSpent(sum.toFixed(0));
         setBudgetUsage((sum / category.budgetAmount) * 100);
      }
   }, [transactions, category.categoryId, category.budgetAmount]);

   const budgetStatus = getBudgetStatus(parseFloat(totalAmountSpent), category.budgetAmount);

   const handleDeleteCategory = async () => {
      setShowConfirmDelete(false);
      removeCategory(category.categoryId);
      await deleteCategory(category.categoryId);
      await fetchCategoryType(category.categoryType.categoryTypeId);
      setAlert("Category deleted successfully", "success");
   };

   const handleRenameCategory = () => {
      handleShowRenameCategoryModal(category);
   };

   const handleUpdateAllocations = () => {
      handleShowUpdateAllocationsModal(category.categoryType);
   };

   const activeFilterCount = [
      Boolean(nameQuery),
      amountMode === "exact" ? Boolean(exactAmount) : Boolean(minAmount || maxAmount),
      dateMode === "exact" ? Boolean(exactDate) : Boolean(startDate || endDate),
   ].filter(Boolean).length;

   const handleResetFilters = () => {
      setNameQuery("");
      setExactAmount("");
      setMinAmount("");
      setMaxAmount("");
      setExactDate("");
      setStartDate("");
      setEndDate("");
   };

   // Consolidated Transaction Filter Logic
   const matchingTransactions = filteredTransactions.filter((tx) => {
      // 1. Name / Merchant Match
      if (nameQuery && nameQuery.trim() !== "") {
         const query = nameQuery.trim().toLowerCase();
         const txName = (tx.name || "").toLowerCase();
         if (!txName.includes(query)) return false;
      }

      // 2. Amount Match (Exact or Range)
      if (amountMode === "exact") {
         if (exactAmount !== "" && !isNaN(parseFloat(exactAmount))) {
            if (Math.abs(tx.amount - parseFloat(exactAmount)) > 0.009) return false;
         }
      } else {
         if (minAmount !== "" && !isNaN(parseFloat(minAmount))) {
            if (tx.amount < parseFloat(minAmount)) return false;
         }
         if (maxAmount !== "" && !isNaN(parseFloat(maxAmount))) {
            if (tx.amount > parseFloat(maxAmount)) return false;
         }
      }

      // 3. Date Match (Exact or Range)
      const txDateStr = tx.date ? String(tx.date) : "";
      if (dateMode === "exact") {
         if (exactDate && txDateStr !== exactDate) return false;
      } else {
         if (startDate && txDateStr < startDate) return false;
         if (endDate && txDateStr > endDate) return false;
      }

      return true;
   });

   return (
      <div className={`relative rounded-2xl shadow-xl p-6 mx-auto w-full max-w-4xl border transition-all duration-300 ${
         isDark
            ? "bg-slate-900/80 border-slate-700/70 text-slate-100"
            : "bg-white border-slate-200 text-slate-800"
      }`}>
         {/* Direct Trash Delete Button */}
         <button
            onClick={() => setShowConfirmDelete(true)}
            className={`absolute top-5 right-5 p-2 rounded-xl transition-all duration-200 border ${
               isDark
                  ? "text-slate-400 hover:text-red-400 bg-slate-800/80 hover:bg-red-500/10 border-slate-700 hover:border-red-500/30"
                  : "text-slate-400 hover:text-red-600 bg-slate-100 hover:bg-red-50 border-slate-200 hover:border-red-200"
            }`}
            title="Delete Category"
         >
            <FaTrashAlt size={14} />
         </button>

         {/* Category Header: Click-to-Edit Name */}
         <div className="flex flex-col items-center justify-center mb-4">
            <h3
               onClick={handleRenameCategory}
               className={`text-2xl md:text-3xl font-extrabold flex items-center gap-2 cursor-pointer transition-colors group ${
                  isDark ? "text-white hover:text-indigo-400" : "text-slate-900 hover:text-indigo-600"
               }`}
               title="Click to rename category"
            >
               {category.name}
               <FaPen size={12} className="opacity-0 group-hover:opacity-100 text-indigo-500 transition-opacity" />
            </h3>
         </div>

         {/* Spent & Clickable Allocation Target Pill */}
         <div className={`text-center mb-3 font-semibold text-lg md:text-xl ${isDark ? "text-slate-200" : "text-slate-700"}`}>
            Spent{" "}
            <span className={`font-black ${budgetStatus.textClass}`}>
               ${totalAmountSpent}
            </span>{" "}
            out of allocated{" "}
            <button
               type="button"
               onClick={handleUpdateAllocations}
               className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full font-bold text-sm border transition-all cursor-pointer ${
                  isDark
                     ? "bg-indigo-500/20 text-indigo-300 border-indigo-500/30 hover:bg-indigo-500/30"
                     : "bg-indigo-50 text-indigo-700 border-indigo-200 hover:bg-indigo-100"
               }`}
               title="Click to adjust allocations"
            >
               ${category.budgetAmount.toFixed(0)}
               <FaPen size={10} className="opacity-70" />
            </button>
         </div>

         {/* Budget Utilization Progress Bar */}
         <div className={`w-full rounded-full h-3.5 mb-6 ${isDark ? "bg-slate-800" : "bg-slate-200"}`}>
            <div
               className={`h-3.5 rounded-full transition-all duration-500 ease-in-out ${budgetStatus.colorClass}`}
               style={{ width: `${budgetUsage > 100 ? 100 : budgetUsage}%` }}
            ></div>
         </div>

         {/* Consolidated Dropdown Filter Header */}
         <div className="flex items-center justify-between mb-4">
            <h4 className={`text-xs font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               Transactions ({matchingTransactions.length})
            </h4>

            {/* Consolidated Dropdown Filter Trigger (Triple Sliders Icon) */}
            <div className="relative">
               <button
                  type="button"
                  onClick={() => setShowFilterDropdown(!showFilterDropdown)}
                  className={`flex items-center gap-2 px-3 py-1.5 rounded-xl text-xs font-bold border transition-all ${
                     showFilterDropdown || activeFilterCount > 0
                        ? "bg-indigo-600 text-white border-indigo-500 shadow-md"
                        : isDark
                           ? "bg-slate-800 text-slate-300 border-slate-700 hover:bg-slate-700 hover:text-white"
                           : "bg-slate-100 text-slate-700 border-slate-300 hover:bg-slate-200"
                  }`}
                  title="Filter Transactions"
               >
                  <FaSlidersH size={13} />
                  <span>Filter</span>
                  {activeFilterCount > 0 && (
                     <span className="ml-1 px-1.5 py-0.2 rounded-full text-[10px] bg-white text-indigo-700 font-extrabold">
                        {activeFilterCount}
                     </span>
                  )}
               </button>

               {/* Consolidated Dropdown Filter Menu */}
               {showFilterDropdown && (
                  <div className={`absolute right-0 mt-2 w-80 p-4 rounded-2xl border shadow-2xl z-30 animate-fade-in ${
                     isDark ? "bg-slate-900 border-slate-700 text-slate-100" : "bg-white border-slate-200 text-slate-800"
                  }`}>
                     <div className="flex items-center justify-between pb-3 mb-3 border-b border-slate-700/50">
                        <span className="text-xs font-bold uppercase tracking-wider flex items-center gap-1.5">
                           <FaSlidersH className="text-indigo-400" size={12} />
                           Filter Transactions
                        </span>
                        {activeFilterCount > 0 && (
                           <button
                              type="button"
                              onClick={handleResetFilters}
                              className="text-[11px] font-bold text-red-400 hover:text-red-300 transition-colors"
                           >
                              Reset All
                           </button>
                        )}
                     </div>

                     <div className="space-y-3 text-left">
                        {/* 1. Name Search */}
                        <div>
                           <label className="text-[10px] font-bold uppercase text-slate-400 mb-1 block">
                              Name / Merchant
                           </label>
                           <div className="relative flex items-center">
                              <FaSearch className={`absolute left-2.5 text-xs ${isDark ? "text-slate-500" : "text-slate-400"}`} />
                              <input
                                 type="text"
                                 placeholder="Search name..."
                                 value={nameQuery}
                                 onChange={(e) => setNameQuery(e.target.value)}
                                 className={`pl-8 pr-7 py-1.5 w-full rounded-lg text-xs font-medium focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                                    isDark ? "bg-slate-800 border border-slate-700 text-white placeholder-slate-500" : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400"
                                 }`}
                              />
                              {nameQuery && (
                                 <button
                                    type="button"
                                    onClick={() => setNameQuery("")}
                                    className="absolute right-2 text-slate-400 hover:text-white"
                                 >
                                    <FaTimes size={10} />
                                 </button>
                              )}
                           </div>
                        </div>

                        {/* 2. Amount Filtering (Exact vs Range Toggle) */}
                        <div>
                           <div className="flex items-center justify-between mb-1">
                              <label className="text-[10px] font-bold uppercase text-slate-400">
                                 Amount ($)
                              </label>
                              <div className="flex items-center gap-1 bg-slate-800/60 p-0.5 rounded-md border border-slate-700">
                                 <button
                                    type="button"
                                    onClick={() => setAmountMode("range")}
                                    className={`px-1.5 py-0.5 text-[9px] font-bold rounded ${
                                       amountMode === "range" ? "bg-indigo-600 text-white" : "text-slate-400"
                                    }`}
                                 >
                                    Range
                                 </button>
                                 <button
                                    type="button"
                                    onClick={() => setAmountMode("exact")}
                                    className={`px-1.5 py-0.5 text-[9px] font-bold rounded ${
                                       amountMode === "exact" ? "bg-indigo-600 text-white" : "text-slate-400"
                                    }`}
                                 >
                                    Exact
                                 </button>
                              </div>
                           </div>

                           {amountMode === "exact" ? (
                              <input
                                 type="number"
                                 step="0.01"
                                 placeholder="Exact amount ($)"
                                 value={exactAmount}
                                 onChange={(e) => setExactAmount(e.target.value)}
                                 className={`px-2.5 py-1.5 w-full rounded-lg text-xs focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                                    isDark ? "bg-slate-800 border border-slate-700 text-white" : "bg-slate-50 border border-slate-300 text-slate-900"
                                 }`}
                              />
                           ) : (
                              <div className="grid grid-cols-2 gap-2">
                                 <input
                                    type="number"
                                    step="0.01"
                                    placeholder="Min ($)"
                                    value={minAmount}
                                    onChange={(e) => setMinAmount(e.target.value)}
                                    className={`px-2.5 py-1.5 w-full rounded-lg text-xs focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                                       isDark ? "bg-slate-800 border border-slate-700 text-white" : "bg-slate-50 border border-slate-300 text-slate-900"
                                    }`}
                                 />
                                 <input
                                    type="number"
                                    step="0.01"
                                    placeholder="Max ($)"
                                    value={maxAmount}
                                    onChange={(e) => setMaxAmount(e.target.value)}
                                    className={`px-2.5 py-1.5 w-full rounded-lg text-xs focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                                       isDark ? "bg-slate-800 border border-slate-700 text-white" : "bg-slate-50 border border-slate-300 text-slate-900"
                                    }`}
                                 />
                              </div>
                           )}
                        </div>

                        {/* 3. Date Filtering (Exact vs Range Toggle) */}
                        <div>
                           <div className="flex items-center justify-between mb-1">
                              <label className="text-[10px] font-bold uppercase text-slate-400">
                                 Date
                              </label>
                              <div className="flex items-center gap-1 bg-slate-800/60 p-0.5 rounded-md border border-slate-700">
                                 <button
                                    type="button"
                                    onClick={() => setDateMode("range")}
                                    className={`px-1.5 py-0.5 text-[9px] font-bold rounded ${
                                       dateMode === "range" ? "bg-indigo-600 text-white" : "text-slate-400"
                                    }`}
                                 >
                                    Range
                                 </button>
                                 <button
                                    type="button"
                                    onClick={() => setDateMode("exact")}
                                    className={`px-1.5 py-0.5 text-[9px] font-bold rounded ${
                                       dateMode === "exact" ? "bg-indigo-600 text-white" : "text-slate-400"
                                    }`}
                                 >
                                    Exact
                                 </button>
                              </div>
                           </div>

                           {dateMode === "exact" ? (
                              <input
                                 type="date"
                                 value={exactDate}
                                 style={{ colorScheme: isDark ? "dark" : "light" }}
                                 onChange={(e) => setExactDate(e.target.value)}
                                 className={`px-2.5 py-1.5 w-full rounded-lg text-xs focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                                    isDark ? "bg-slate-800 border border-slate-700 text-white" : "bg-slate-50 border border-slate-300 text-slate-900"
                                 }`}
                              />
                           ) : (
                              <div className="grid grid-cols-2 gap-2">
                                 <input
                                    type="date"
                                    value={startDate}
                                    style={{ colorScheme: isDark ? "dark" : "light" }}
                                    onChange={(e) => setStartDate(e.target.value)}
                                    className={`px-2 py-1.5 w-full rounded-lg text-xs focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                                       isDark ? "bg-slate-800 border border-slate-700 text-white" : "bg-slate-50 border border-slate-300 text-slate-900"
                                    }`}
                                 />
                                 <input
                                    type="date"
                                    value={endDate}
                                    style={{ colorScheme: isDark ? "dark" : "light" }}
                                    onChange={(e) => setEndDate(e.target.value)}
                                    className={`px-2 py-1.5 w-full rounded-lg text-xs focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
                                       isDark ? "bg-slate-800 border border-slate-700 text-white" : "bg-slate-50 border border-slate-300 text-slate-900"
                                    }`}
                                 />
                              </div>
                           )}
                        </div>
                     </div>
                  </div>
               )}
            </div>
         </div>

         {/* Transactions Grid */}
         <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3 max-h-72 overflow-y-auto scrollbar-hide pr-1">
            {matchingTransactions.length > 0 ? (
               matchingTransactions.map((transaction) => (
                  <DetailedCategoryTransaction
                     key={transaction.transactionId}
                     transaction={transaction}
                     handleShowSplitTransactionModal={handleShowSplitTransactionModal}
                     handleShowReduceTransactionModal={handleShowReduceTransactionModal}
                     handleShowRenameTransactionModal={handleShowRenameTransactionModal}
                     handleShowAssignCategoryModal={handleShowAssignCategoryModal}
                  />
               ))
            ) : (
               <div className={`col-span-full text-center py-6 text-sm font-semibold rounded-xl border ${
                  isDark ? "bg-slate-800/40 border-slate-800 text-slate-500" : "bg-slate-50 border-slate-200 text-slate-400"
               }`}>
                  {activeFilterCount > 0
                     ? "No transactions match your current filter criteria."
                     : "No transactions assigned to this category."
                  }
               </div>
            )}
         </div>

         {/* Confirmation Modal for Category Deletion */}
         {showConfirmDelete && (
            <ConfirmationModal
               question={`Are you sure you want to delete the category "${category.name}"?`}
               onConfirm={handleDeleteCategory}
               onClose={() => setShowConfirmDelete(false)}
            />
         )}
      </div>
   );
};

export default DetailedCategory;
