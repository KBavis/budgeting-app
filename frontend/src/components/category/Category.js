import React, { useContext, useEffect, useState } from "react";
import { useDrop } from "react-dnd";
import Transaction from "../transaction/Transaction";
import transactionContext from "../../context/transaction/transactionContext";
import CategoryDropdown from "../layout/CategoryDropdown";
import categoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import AlertContext from "../../context/alert/alertContext";

/**
 * Component to store all relevant information for Category entity on Home Page
 *
 * @param category
 *       - Category entity to generate component for
 * @param handleShowSplitTransactionModal
 *       - functionality to show SplitTransaction modal
 * @param handleShowReduceTransactionModal
 *       - functionality to show ReduceTransaction modal
 * @param handleShowRenameTransactionModal
 *       - functionality to show RenameTransactionModal
 * @param handleShowAssignCategoryModal
 *       - functionlaity to show AssignCategoryModal
 * @param handleDeleteCategory
 *       - function to handle deleting a category
 * @param handleRenameCategory
 *       - function to handle renaming a category
 * @param handleUpdateAllocations
 *       - function to handle updating category allocations
 * @returns
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
   const { transactions, updateCategory, removeCategory } =
      useContext(transactionContext);
   const { deleteCategory } = useContext(categoryContext);
   const { fetchCategoryType } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);

   // Local State
   const [recentTransactions, setRecentTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [budgetUsage, setBudgetUsage] = useState(0);
   const [budgetAllocation, setBudgetAllocation] = useState(0);

   //Functions
   const handleDeleteCategory = async () => {
      removeCategory(category.categoryId); //update correlated Transactions
      await deleteCategory(category.categoryId); //remove from backend
      await fetchCategoryType(category.categoryType.categoryTypeId); //fetch updated Category type
      setAlert("Category deleted successfully", "success");
   };

   const handleRenameCategory = () => {
      handleShowRenameCategoryModal(category);
   };

   const handleUpdateAllocations = () => {
      console.log("Handle Update Allocations Modal Clicked");
      handleShowUpdateAllocationsModal(category.categoryType);
   };

   // Functionality to allow a Transaction to be assigned to a Category
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
         // Filter Transactions corresponding to current Category
         const filtered = transactions.filter(
            (transaction) =>
               transaction.category &&
               transaction.category.categoryId === category.categoryId
         );

         // Set three most recent Transactions for Category
         const mostRecent = filtered.slice(0, 3);
         setRecentTransactions(mostRecent);

         // Sum total amount of Transactions corresponding to entity
         const sum = filtered.reduce((acc, transaction) => {
            return acc + transaction.amount;
         }, 0);

         // Set Total Amount & Budget Usage Percentage
         setTotalAmountSpent(sum.toFixed(0));
         setBudgetUsage((sum / category.budgetAmount) * 100);
      } else {
         setRecentTransactions([]);
      }
   }, [transactions, category.categoryId]);

   // Set Budget Allocation when Category component initially loaded
   useEffect(() => {
      setBudgetAllocation(category.budgetAmount.toFixed(0));
   }, [category]);


   // Function to determine progress bar for our Category entity
   const getProgressBarColor = () => {
      const percentage = budgetUsage;
      if (percentage <= 50) {
         return "bg-green-500";
      } else if (percentage <= 70) {
         return "bg-yellow-500";
      } else if (percentage <= 90) {
         return "bg-orange-500";
      } else {
         return "bg-red-500";
      }
   };

   return (
      <div
         ref={drop}
         className={`bg-gray-200 rounded-lg shadow-md p-4 relative w-full py-4 ${isOver ? " bg-indigo-200" : canDrop ? " bg-indigo-100" : ""} xs:p-2 xs:w-11/12 xs:py-6`}
      >
         <div className="flex justify-between items-center mb-2">
            <h4 className="text-xl font-bold text-gray-800 xs:text-lg truncate flex-grow">
               {category.name}
            </h4>
            <CategoryDropdown
               handleDeleteCategory={handleDeleteCategory}
               handleRenameCategory={handleRenameCategory}
               handleUpdateAllocations={handleUpdateAllocations}
               className="ml-auto flex-shrink-0"
            />
         </div>
         <div className="mb-2 text-sm font-semibold text-gray-600 xs:text-xs">
            <p>Allocated Budget: ${budgetAllocation}</p>
            <p>Total Spent: ${totalAmountSpent}</p>
         </div>
         <div className="w-full bg-gray-300 rounded-full h-4 mb-4 xs:h-3">
            <div
               className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()} xs:h-3`}
               style={{ width: `${budgetUsage > 100 ? 100 : budgetUsage}%` }}
            ></div>
         </div>
         <div className="space-y-2">
            {recentTransactions?.map((transaction) => (
               <Transaction
                  key={transaction.transactionId}
                  transaction={transaction}
                  handleShowSplitTransactionModal={
                     handleShowSplitTransactionModal
                  }
                  handleShowReduceTransactionModal={
                     handleShowReduceTransactionModal
                  }
                  handleShowRenameTransactionModal={
                     handleShowRenameTransactionModal
                  }
                  handleShowAssignCategoryModal={handleShowAssignCategoryModal}
               />
            ))}
         </div>
      </div>
   );
};

export default Category;
