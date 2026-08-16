import React, { useContext, useState, useEffect, useRef } from "react";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import categoryContext from "../context/category/categoryContext";
import DetailedCategory from "../components/category/DetailedCategory";
import authContext from "../context/auth/authContext";
import transactionContext from "../context/transaction/transactionContext";
import AlertContext from "../context/alert/alertContext";
import SplitTransactionModal from "../components/transaction/SplitTransaction";
import ReduceTransaction from "../components/transaction/ReduceTransaction";
import RenameTransaction from "../components/transaction/RenameTransaction";
import AssignCategoryModal from "../components/transaction/AssignCategoryModal";
import UpdateAllocationsModal from "../components/category/UpdateAllocationsModal";
import RenameCategory from "../components/category/RenameCategory";
import Loading from "../components/util/Loading";
import { ThemeContext } from "../context/theme/ThemeContext";
import { FaSearch, FaTimes, FaSlidersH, FaCheck, FaPen } from "react-icons/fa";

import UpdateCategoryTypeAllocationsModal from "../components/category/types/UpdateCategoryTypeAllocationsModal";

/**
 * CategoryType Page for all corresponding Categories
 * associated with CategoryType with full Light/Dark mode support
 */
const CategoryTypePage = ({ categoryType }) => {
   const { categoryTypes, fetchCategoryTypes, updateCategoryType } = useContext(categoryTypeContext);
   const { categories, fetchCategories } = useContext(categoryContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { transactions, fetchTransactions } = useContext(transactionContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const [filteredCategories, setFilteredCategories] = useState([]);
   const [categorySearchQuery, setCategorySearchQuery] = useState("");
   const [showCategoryFilterMenu, setShowCategoryFilterMenu] = useState(false);

   const [showSplitTransactionModal, setShowSplitTransactionModal] = useState(false);
   const [selectedCategoryType, setSelectedCategoryType] = useState(null);
   const [showReduceTransactionModal, setShowReduceTransactionModal] = useState(false);
   const [showRenameTransactionModal, setShowRenameTransactionModal] = useState(false);
   const [showAssignCategoryModal, setShowAssignCategoryModal] = useState(false);
   const [showUpdateAllocationsModal, setShowUpdateAllocationsModal] = useState(false);
   const [showRenameCategoryModal, setShowRenameCategoryModal] = useState(false);
   const [showUpdateCategoryTypeAllocationsModal, setShowUpdateCategoryTypeAllocationsModal] = useState(false);
   const [currentTransaction, setCurrentTransaction] = useState(null);
   const [selectedCategory, setSelectedCategory] = useState(null);
   const [isLoading, setIsLoading] = useState(true);

   const currentCategoryType = (categoryTypes || []).find(
       (ct) => ct.name.toLowerCase() === categoryType.toLowerCase()
   );

   const rawAllocationPct = currentCategoryType?.budgetAllocationPercentage || 0;
   const allocationPct = Math.round(
      rawAllocationPct > 1 ? rawAllocationPct : rawAllocationPct * 100
   );
   const [isEditingAllocation, setIsEditingAllocation] = useState(false);
   const [editPct, setEditPct] = useState(allocationPct);

   useEffect(() => {
      setEditPct(allocationPct);
   }, [allocationPct]);

   const handleConfirmAllocation = async () => {
      if (!currentCategoryType) return;
      const parsedPct = parseFloat(editPct);
      if (isNaN(parsedPct) || parsedPct < 0 || parsedPct > 100) {
         setAlert("Please enter a valid allocation percentage between 0 and 100", "danger");
         setEditPct(allocationPct);
         setIsEditingAllocation(false);
         return;
      }

      if (parsedPct !== allocationPct) {
         await updateCategoryType(currentCategoryType.categoryTypeId, {
            budgetAllocationPercentage: parsedPct / 100.0,
         });
         setAlert(`Updated ${currentCategoryType.name} allocation to ${parsedPct}%`, "success");
      }
      setIsEditingAllocation(false);
   };

   const handleCancelAllocation = () => {
      setEditPct(allocationPct);
      setIsEditingAllocation(false);
   };

   const initialFetchRef = useRef(false);

   // Filter The Categories for Current Category Type
   useEffect(() => {
      if (!categoryTypes || !categories) return;

      const currentCategoryType = categoryTypes.find(
          (ct) => ct.name.toLowerCase() === categoryType.toLowerCase()
      );

      if (currentCategoryType) {
         const filtered = categories.filter(
             (category) =>
                 category.categoryTypeId === currentCategoryType.categoryTypeId
         );
         setFilteredCategories(filtered);
         setIsLoading(false);
      } else if (categoryTypes.length > 0) {
         setFilteredCategories([]);
         setIsLoading(false);
      }
   }, [categoryTypes, categories, categoryType]);

   // Fetch All Values On Refresh
   useEffect(() => {
      const fetch = async () => {
         if (!user && localStorage.token) {
            await fetchAuthenticatedUser();
         }

         if (!categoryTypes || categoryTypes.length === 0) {
            await fetchCategoryTypes();
         }

         if (!categories || categories.length === 0) {
            await fetchCategories();
         }

         if (!transactions || transactions.length === 0) {
            await fetchTransactions();
         }
      };

      if (!initialFetchRef.current) {
         fetch();
         initialFetchRef.current = true;
      }
   }, [
      user,
      fetchAuthenticatedUser,
      categoryTypes,
      fetchCategoryTypes,
      categories,
      fetchCategories,
      transactions,
      fetchTransactions,
   ]);

   // Modal handlers
   const handleShowSplitTransactionModal = (transaction) => {
      setShowSplitTransactionModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseSplitTransactionModal = () => {
      setShowSplitTransactionModal(false);
   };

   const handleShowReduceTransactionModal = (transaction) => {
      setShowReduceTransactionModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseReduceTransactionModal = () => {
      setShowReduceTransactionModal(false);
   };

   const handleShowRenameTransactionModal = (transaction) => {
      setShowRenameTransactionModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseRenameTransactionModal = () => {
      setShowRenameTransactionModal(false);
   };

   const handleShowAssignCategoryModal = (transaction) => {
      setShowAssignCategoryModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseAssignCategoryModal = () => {
      setShowAssignCategoryModal(false);
   };

   const handleShowUpdateAllocationsModal = (type) => {
      setSelectedCategoryType(type);
      setShowUpdateAllocationsModal(true);
   };

   const handleCloseUpdateAllocationsModal = () => {
      setShowUpdateAllocationsModal(false);
   };

   const handleShowRenameCategoryModal = (category) => {
      setSelectedCategory(category);
      setShowRenameCategoryModal(true);
   };

   const handleCloseRenameCategoryModal = () => {
      setShowRenameCategoryModal(false);
   };

   const filteredCategoriesByQuery = filteredCategories.filter((category) =>
       (category.name || "").toLowerCase().includes(categorySearchQuery.trim().toLowerCase())
   );

   return (
       <div className={`flex flex-col min-h-screen relative ${
          isDark
             ? "bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 text-slate-100"
             : "bg-gradient-to-br from-slate-100 via-indigo-50/50 to-slate-100 text-slate-800"
       }`}>
          <div className="flex-1 flex flex-col justify-center items-center px-4 md:px-12 pt-16 pb-12">
             {/* Header Container with Increased Spacing */}
             <div className="max-w-xl w-full text-center mb-10 mt-4">
                <h2 className={`text-4xl md:text-5xl font-black mb-3 ${isDark ? "text-white" : "text-slate-900"}`}>
                   Explore <span className={isDark ? "text-indigo-400" : "text-indigo-600"}>{categoryType}</span>
                </h2>
                <p className={`text-xs md:text-sm font-medium mb-4 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                   View and manage categories and assigned transactions under your {categoryType} allocation.
                </p>

                <div className="flex items-center justify-center gap-2 mb-6">
                   {isEditingAllocation ? (
                      <div className="flex items-center gap-1.5 bg-indigo-500/10 border border-indigo-500/40 px-3 py-1 rounded-xl">
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
                            className={`w-16 px-2 py-0.5 text-xs font-bold text-center rounded-lg border focus:outline-none focus:ring-1 focus:ring-indigo-500 ${
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
                            <FaCheck size={10} />
                         </button>
                         <button
                            type="button"
                            onClick={handleCancelAllocation}
                            className={`p-1 rounded-md transition-all ${
                               isDark ? "bg-slate-800 text-slate-400 hover:text-white" : "bg-slate-200 text-slate-600 hover:text-slate-900"
                            }`}
                            title="Cancel"
                         >
                            <FaTimes size={10} />
                         </button>
                      </div>
                   ) : (
                      <button
                         type="button"
                         onClick={() => setIsEditingAllocation(true)}
                         className={`inline-flex items-center gap-2 px-3.5 py-1.5 rounded-xl text-xs font-bold border transition-all cursor-pointer group ${
                            isDark
                               ? "bg-indigo-500/20 text-indigo-300 border-indigo-500/30 hover:bg-indigo-500/30 hover:border-indigo-400"
                               : "bg-indigo-50 text-indigo-700 border-indigo-200 hover:bg-indigo-100 hover:border-indigo-300 shadow-sm"
                         }`}
                         title="Click to edit target allocation percentage"
                      >
                         <span>{allocationPct}% Target Income Allocation</span>
                         <FaPen size={10} className="opacity-60 group-hover:opacity-100 transition-opacity" />
                      </button>
                   )}
                </div>

                {/* Dropdown-style Filter Toggle Button */}
                <div className="flex flex-col items-center justify-center relative">
                   <button
                      type="button"
                      onClick={() => setShowCategoryFilterMenu(!showCategoryFilterMenu)}
                      className={`inline-flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold border transition-all duration-200 ${
                         showCategoryFilterMenu || categorySearchQuery
                            ? "bg-indigo-600 text-white border-indigo-500 shadow-md"
                            : isDark
                               ? "bg-slate-800/80 text-slate-300 border-slate-700 hover:bg-slate-800 hover:text-white"
                               : "bg-white text-slate-700 border-slate-300 hover:bg-slate-50 shadow-sm"
                      }`}
                   >
                      <FaSlidersH size={12} />
                      <span>Filter Categories</span>
                      {categorySearchQuery && (
                         <span className="w-2 h-2 rounded-full bg-indigo-300 animate-pulse" />
                      )}
                   </button>

                   {/* Expandable Category Search Input Menu */}
                   {showCategoryFilterMenu && (
                      <div className={`mt-3 w-full max-w-sm p-3 rounded-2xl border shadow-xl animate-fade-in transition-all ${
                         isDark ? "bg-slate-900/95 border-slate-700 text-slate-100" : "bg-white border-slate-200 text-slate-800"
                      }`}>
                         <div className="relative flex items-center">
                            <FaSearch className={`absolute left-3 text-xs ${isDark ? "text-slate-500" : "text-slate-400"}`} />
                            <input
                               type="text"
                               autoFocus
                               placeholder="Search category name..."
                               value={categorySearchQuery}
                               onChange={(e) => setCategorySearchQuery(e.target.value)}
                               className={`pl-9 pr-8 py-2 w-full rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                                  isDark ? "bg-slate-800 border border-slate-700 text-white placeholder-slate-500" : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400"
                               }`}
                            />
                            {categorySearchQuery && (
                               <button
                                  type="button"
                                  onClick={() => setCategorySearchQuery("")}
                                  className="absolute right-2.5 text-slate-400 hover:text-red-400 transition-colors"
                                  title="Clear"
                               >
                                  <FaTimes size={11} />
                               </button>
                            )}
                         </div>
                         {categorySearchQuery && (
                            <p className="text-[11px] font-semibold mt-2 text-indigo-400 text-left px-1">
                               Showing {filteredCategoriesByQuery.length} of {filteredCategories.length} categories
                            </p>
                         )}
                      </div>
                   )}
                </div>
             </div>

             {isLoading ? (
                <div className="flex justify-center items-center py-16">
                   <Loading />
                </div>
             ) : (
                <div className="w-full max-w-7xl xl:max-w-[1600px] flex-1 pb-16">
                   {filteredCategoriesByQuery.length > 0 ? (
                       <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                           {filteredCategoriesByQuery.map((category) => (
                               <DetailedCategory
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
                   ) : (
                       <div className={`flex flex-col items-center justify-center w-full max-w-xl py-12 px-6 text-center rounded-2xl border ${
                          isDark ? "bg-slate-900/40 border-slate-800 text-slate-400" : "bg-white border-slate-200 text-slate-500 shadow-sm"
                       }`}>
                          <h3 className={`text-xl md:text-2xl font-bold mb-2 ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                             No categories found
                          </h3>
                          <p className="text-sm">
                             {categorySearchQuery ? `No category matching "${categorySearchQuery}"` : "Try creating a new category for this category type."}
                          </p>
                       </div>
                   )}
                </div>
             )}
          </div>

          {/* Modals */}
          {showSplitTransactionModal && (
              <SplitTransactionModal
                  onClose={handleCloseSplitTransactionModal}
                  transaction={currentTransaction}
              />
          )}
          {showReduceTransactionModal && (
              <ReduceTransaction
                  onClose={handleCloseReduceTransactionModal}
                  transaction={currentTransaction}
              />
          )}
          {showRenameTransactionModal && (
              <RenameTransaction
                  onClose={handleCloseRenameTransactionModal}
                  transaction={currentTransaction}
              />
          )}
          {showAssignCategoryModal && (
              <AssignCategoryModal
                  onClose={handleCloseAssignCategoryModal}
                  transaction={currentTransaction}
              />
          )}
          {showUpdateAllocationsModal && (
              <UpdateAllocationsModal
                  onClose={handleCloseUpdateAllocationsModal}
                  categoryType={selectedCategoryType}
              />
          )}
          {showRenameCategoryModal && (
              <RenameCategory
                  onClose={handleCloseRenameCategoryModal}
                  category={selectedCategory}
              />
          )}
          {showUpdateCategoryTypeAllocationsModal && (
              <UpdateCategoryTypeAllocationsModal
                  onClose={() => setShowUpdateCategoryTypeAllocationsModal(false)}
              />
          )}
       </div>
   );
};

export default CategoryTypePage;
