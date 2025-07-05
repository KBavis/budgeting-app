import React, { useState, useEffect, useContext } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import DetailedCategoryTransaction from "../transaction/DetailedCategoryTransaction";
import CategoryDropdown from "../layout/CategoryDropdown";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import categoryContext from "../../context/category/categoryContext";
import AlertContext from "../../context/alert/alertContext";

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
   const [filterQuery, setFilterQuery] = useState("");

   //Global State
   const { transactions, removeCategory } = useContext(transactionContext);
   const { deleteCategory } = useContext(categoryContext);
   const { fetchCategoryType } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);

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
   }, [transactions, category.categoryId]);

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

   const getProgressTextColor = () => {
      const percentage = budgetUsage;
      if (percentage <= 50) {
         return "text-green-500";
      } else if (percentage <= 70) {
         return "text-yellow-500";
      } else if (percentage <= 90) {
         return "text-orange-500";
      } else {
         return "text-red-500";
      }
   };

   const handleDeleteCategory = async () => {
      removeCategory(category.categoryId); //update correlated Transactions
      await deleteCategory(category.categoryId); //remove from backend
      await fetchCategoryType(category.categoryType.categoryTypeId); //fetch updated Category type
      setAlert("Category deleted successfully", "success");
   };

   const handleRenameCategory = () => {
      handleShowRenameCategoryModal(category)
   };

   const handleUpdateAllocations = () => {
      console.log("Handle Update Allocations Clicked!");
      handleShowUpdateAllocationsModal(category.categoryType);
   };

   const filterTransactionsByQuery = filteredTransactions.filter((transaction) =>
      transaction.name.toLowerCase().startsWith(filterQuery.toLowerCase())
   );

   return (
       <div className="relative bg-white rounded-lg shadow-md p-6 mx-4 w-full lg:w-2/3 xs:p-3 xs:mx-1">
          <div className="absolute top-4 right-4 xs:top-2 xs:right-2">
             <CategoryDropdown
                 handleDeleteCategory={handleDeleteCategory}
                 handleRenameCategory={handleRenameCategory}
                 handleUpdateAllocations={handleUpdateAllocations}
             />
          </div>
          <h3 className="text-2xl font-bold mb-2 text-center xs:text-xl">
             {category.name}
          </h3>
          <div className="flex justify-center mb-8 xs:mb-8">
             <input
                 type="text"
                 placeholder="Filter transactions..."
                 value={filterQuery}
                 onChange={(e) => setFilterQuery(e.target.value)}
                 className="p-2 w-full md:w-1/4 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 xs:p-1 xs:text-sm xs:w-11/12 xs:mb-4"
             />
          </div>
          <div className="text-center mb-2 font-semibold text-xl xs:text-base">
             Spent{" "}
             <span className={`font-extrabold ${getProgressTextColor()}`}>
                {" "}
                ${totalAmountSpent}{" "}
            </span>{" "}
             out of allocated
             <span className="text-black font-bold">
                {" "}
                ${category.budgetAmount.toFixed(0)}
            </span>
          </div>
          <div className="w-full bg-gray-300 rounded-full h-4 mb-4">
             <div
                 className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()}`}
                 style={{width: `${budgetUsage > 100 ? 100 : budgetUsage}%`}}
             ></div>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-2 max-h-64 overflow-y-auto xs:grid-cols-1 xs:gap-1">
             {filterTransactionsByQuery.length > 0 ? (
                 filterTransactionsByQuery.map((transaction) => (
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
                 <div className="col-span-full text-center text-gray-500 py-4 xs:text-sm">
                    No transactions found.
                 </div>
             )}

          </div>
       </div>
   );

};

export default DetailedCategory;
