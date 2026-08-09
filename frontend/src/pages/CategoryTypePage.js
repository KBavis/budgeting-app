import React, { useContext, useState, useEffect, useRef } from "react";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import categoryContext from "../context/category/categoryContext";
import DetailedCategory from "../components/category/DetailedCategory";
import authContext from "../context/auth/authContext";
import transactionContext from "../context/transaction/transactionContext";
import SplitTransactionModal from "../components/transaction/SplitTransaction";
import ReduceTransaction from "../components/transaction/ReduceTransaction";
import RenameTransaction from "../components/transaction/RenameTransaction";
import AssignCategoryModal from "../components/transaction/AssignCategoryModal";
import UpdateAllocationsModal from "../components/category/UpdateAllocationsModal";
import RenameCategory from "../components/category/RenameCategory";
import Loading from "../components/util/Loading";
import { ThemeContext } from "../context/theme/ThemeContext";
import { FaSearch, FaTimes, FaSlidersH } from "react-icons/fa";

/**
 * CategoryType Page for all corresponding Categories
 * associated with CategoryType with full Light/Dark mode support
 */
const CategoryTypePage = ({ categoryType }) => {
   const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext);
   const { categories, fetchCategories } = useContext(categoryContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { transactions, fetchTransactions } = useContext(transactionContext);
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
   const [currentTransaction, setCurrentTransaction] = useState(null);
   const [selectedCategory, setSelectedCategory] = useState(null);
   const [isLoading, setIsLoading] = useState(true);

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
                <p className={`text-xs md:text-sm font-medium mb-8 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                   View and manage categories and assigned transactions under your {categoryType} allocation.
                </p>

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
                <div className="flex flex-col items-center w-full space-y-6 max-w-5xl">
                   {filteredCategoriesByQuery.length > 0 ? (
                       filteredCategoriesByQuery.map((category) => (
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
                       ))
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
       </div>
   );
};

export default CategoryTypePage;
