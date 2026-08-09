import React, { useContext, useEffect, useState } from "react";
import { useDrop } from "react-dnd";
import Transaction from "../transaction/Transaction";
import transactionContext from "../../context/transaction/transactionContext";
import categoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import AlertContext from "../../context/alert/alertContext";
import { getBudgetStatus } from "../../utils/budgetColors";
import { FaTrash } from "react-icons/fa";
import ConfirmationModal from "../layout/ConfirmationModal";
import { ThemeContext } from "../../context/theme/ThemeContext";

/**
 * Clean Category component with explicit theme switching support
 * guaranteeing readable text and dark backgrounds in Dark Mode.
 */
const Category = ({
   category,
   handleShowSplitTransactionModal,
   handleShowReduceTransactionModal,
   handleShowRenameTransactionModal,
   handleShowAssignCategoryModal,
   handleShowUpdateAllocationsModal,
   handleShowRenameCategoryModal
}) => {
   // Global State
   const { transactions, updateCategory, removeCategory } = useContext(transactionContext);
   const { deleteCategory } = useContext(categoryContext);
   const { fetchCategoryType, categoryTypes } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   // Local State
   const [recentTransactions, setRecentTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [budgetUsage, setBudgetUsage] = useState(0);
   const [budgetAllocation, setBudgetAllocation] = useState(0);
   const [showConfirmDelete, setShowConfirmDelete] = useState(false);

   // Functions
   const handleDeleteCategory = async () => {
      removeCategory(category.categoryId);
      await deleteCategory(category.categoryId);
      if (category.categoryTypeId) {
         await fetchCategoryType(category.categoryTypeId);
      }
      setAlert("Category deleted successfully", "success");
      setShowConfirmDelete(false);
   };

   const handleRenameCategory = () => {
      handleShowRenameCategoryModal(category);
   };

   const handleUpdateAllocations = () => {
      const ct = (categoryTypes || []).find((t) => t.categoryTypeId === category.categoryTypeId);
      handleShowUpdateAllocationsModal(ct);
   };

   // Allow drag and drop of transactions into Category
   const [{ canDrop, isOver }, drop] = useDrop(() => ({
      accept: "transaction",
      drop: (item) => {
         updateCategory(item.transaction.transactionId, category.categoryId);
      },
      collect: (monitor) => ({
         isOver: !!monitor.isOver(),
         canDrop: !!monitor.canDrop(),
      }),
   }));

   useEffect(() => {
      if (transactions) {
         const filtered = transactions.filter(
            (transaction) =>
               transaction.category &&
               transaction.category.categoryId === category.categoryId
         );

         const mostRecent = filtered.slice(0, 3);
         setRecentTransactions(mostRecent);

         const sum = filtered.reduce((acc, transaction) => {
            return acc + transaction.amount;
         }, 0);

         setTotalAmountSpent(sum.toFixed(0));
         setBudgetUsage((sum / category.budgetAmount) * 100);
      } else {
         setRecentTransactions([]);
      }
   }, [transactions, category.categoryId]);

   useEffect(() => {
      setBudgetAllocation(category.budgetAmount.toFixed(0));
   }, [category]);

   const budgetStatus = getBudgetStatus(parseFloat(totalAmountSpent), category.budgetAmount);

   return (
      <>
         <div
            ref={drop}
            className={`rounded-xl p-4 relative w-full border transition-all duration-300 ${
               isDark
                  ? "bg-slate-800/90 border-slate-700/80 text-slate-100 shadow-md hover:border-slate-600"
                  : "bg-white border-slate-200/90 text-slate-800 shadow-sm hover:shadow-md hover:border-slate-300"
            } ${
               isOver
                  ? isDark ? "bg-brand-900/60 border-brand-500 scale-[1.01]" : "bg-brand-50 border-brand-500 scale-[1.01]"
                  : canDrop ? "border-brand-500/50" : ""
            }`}
         >
            {/* Header: Clickable Category Name + Trash Delete Icon */}
            <div className="flex justify-between items-center mb-2">
               <button
                  type="button"
                  onClick={handleRenameCategory}
                  className={`text-base font-bold truncate px-2 py-0.5 -ml-2 rounded-lg transition-all text-left ${
                     isDark
                        ? "text-white hover:bg-slate-700/60"
                        : "text-slate-900 hover:bg-slate-100"
                  }`}
                  title="Click to rename category"
               >
                  {category.name}
               </button>

               <button
                  type="button"
                  onClick={() => setShowConfirmDelete(true)}
                  className={`p-1.5 rounded-lg transition-colors ml-auto flex-shrink-0 opacity-60 hover:opacity-100 ${
                     isDark
                        ? "text-slate-400 hover:text-red-400 hover:bg-slate-700"
                        : "text-slate-400 hover:text-red-500 hover:bg-slate-100"
                  }`}
                  title="Delete category"
               >
                  <FaTrash className="w-3.5 h-3.5" />
               </button>
            </div>

            {/* Clickable Budget Allocation Pill & Spent Display */}
            <div className="flex justify-between items-center mb-2 text-xs">
               <button
                  type="button"
                  onClick={handleUpdateAllocations}
                  className={`border rounded-md px-2 py-0.5 transition-all text-left font-medium ${
                     isDark
                        ? "bg-slate-700/60 text-slate-300 border-slate-600 hover:bg-slate-700"
                        : "bg-slate-100 text-slate-600 border-slate-200 hover:bg-slate-200"
                  }`}
                  title="Click to edit budget allocation"
               >
                  Budget: <strong className={isDark ? "text-white font-bold" : "text-slate-900 font-bold"}>${budgetAllocation}</strong>
               </button>

               <span className={isDark ? "text-slate-400" : "text-slate-500"}>
                  Spent: <strong className={budgetStatus.textClass}>${totalAmountSpent}</strong>
               </span>
            </div>

            {/* Budget Usage Bar */}
            <div className={`w-full rounded-full h-2 mb-3 overflow-hidden ${isDark ? "bg-slate-900" : "bg-slate-200"}`}>
               <div
                  className={`h-2 rounded-full transition-all duration-500 ease-in-out ${budgetStatus.colorClass}`}
                  style={{ width: `${budgetUsage > 100 ? 100 : budgetUsage}%` }}
               />
            </div>

            {/* Recent Transactions list */}
            {recentTransactions && recentTransactions.length > 0 && (
               <div className="space-y-1.5 pt-1">
                  {recentTransactions.map((transaction) => (
                     <Transaction
                        key={transaction.transactionId}
                        transaction={transaction}
                        handleShowSplitTransactionModal={handleShowSplitTransactionModal}
                        handleShowReduceTransactionModal={handleShowReduceTransactionModal}
                        handleShowRenameTransactionModal={handleShowRenameTransactionModal}
                        handleShowAssignCategoryModal={handleShowAssignCategoryModal}
                     />
                  ))}
               </div>
            )}
         </div>

         {/* Delete Confirmation Modal */}
         {showConfirmDelete && (
            <ConfirmationModal
               question={`Are you sure you want to delete the category "${category.name}"?`}
               onConfirm={handleDeleteCategory}
               onClose={() => setShowConfirmDelete(false)}
            />
         )}
      </>
   );
};

export default Category;
