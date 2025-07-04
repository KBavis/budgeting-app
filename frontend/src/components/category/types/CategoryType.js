import React, { useContext, useState, useEffect } from "react";
import Category from "../Category";
import transactionContext from "../../../context/transaction/transactionContext";
import categoryContext from "../../../context/category/categoryContext";
import { useNavigate } from "react-router-dom";
import { FaExternalLinkAlt } from "react-icons/fa";

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

   const [filteredCategories, setFilteredCategories] = useState([]);
   const [filteredTransactions, setFilteredTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [totalAmountBudgeted, setTotalAmountBudgeted] = useState(0);
   const [expectedSavings, setExpectedSavings] = useState(0);

   useEffect(() => {
      const filtered = categories.filter(
         (category) =>
            category.categoryType.categoryTypeId === categoryType.categoryTypeId
      );
      setFilteredCategories(filtered);
   }, [categories, categoryType.categoryTypeId]);

   useEffect(() => {
      if (filteredCategories.length > 0) {
         // Calculate total budgeted amount
         const totalBudgeted = filteredCategories.reduce(
            (sum, category) => sum + category.budgetAmount,
            0
         );
         setTotalAmountBudgeted(Math.round(totalBudgeted));
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
      // Calculate expected savings
      const totalBudgeted = totalAmountBudgeted;
      const savedAmount = categoryType.savedAmount || 0; // Assuming savedAmount is provided in categoryType

      // Expected savings is the saved amount plus the difference between total budgeted and total spent
      const calculatedSavings =
         savedAmount + (totalBudgeted - totalAmountSpent);
      setExpectedSavings(calculatedSavings);
   }, [
      totalAmountBudgeted,
      filteredCategories,
      filteredTransactions,
      categoryType.savedAmount,
      totalAmountSpent,
   ]);

   const navigate = useNavigate();

   const percentageUtilized =
      totalAmountBudgeted > 0
         ? Math.min(
              Math.round((totalAmountSpent / totalAmountBudgeted) * 100),
              100
           )
         : 0;

   const getProgressBarColor = () => {
      const percentage = percentageUtilized;
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

   const getSavingsColor = () => {
      return expectedSavings >= 0 ? "text-green-500" : "text-red-500";
   };

   const handleClick = () => {
      navigate(`/category/type/${categoryType.name.toLowerCase()}`);
   };

   return (
      <div className="relative bg-white rounded-lg shadow-md px-4 pb-4 w-11/12 md:w-full h-[400px] overflow-y-auto overflow-x-hidden hover:duration-100 xxl:h-[650px] xs:h-[350px] xs:px-2 xs:pb-2">
         <div className="sticky top-0 z-20 bg-white pb-2 xs:pb-1">
            <div className="flex justify-between items-center">
               <div></div> {/* Placeholder to balance the flex alignment */}
               <h3 className="text-3xl text-center font-bold mb-2 mt-2 flex-grow xs:text-xl">
                  {categoryType.name}
               </h3>
               <button
                  onClick={handleClick}
                  className="text-black font-bold duration-100 hover:scale-1025 hover:cursor-pointer hover:text-indigo-600 xs:text-sm"
               >
                  <FaExternalLinkAlt size={20} className="xs:w-4 xs:h-4"/>
               </button>
            </div>
            <p className="mb-2 text-center font-semibold xs:text-sm">
               Spent: ${totalAmountSpent} / ${totalAmountBudgeted}
            </p>
            <p className="text-center font-semibold mb-6 xs:text-sm">
               Expected Savings:{" "}
               <span className={`font-semibold ${getSavingsColor()}`}>
                  ${expectedSavings.toFixed(2)}
               </span>
            </p>
            <div className="w-full bg-gray-300 rounded-full h-4 mb-4 pb-3 xs:h-3 xs:mb-2 xs:pb-1">
               <div
                  className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()} xs:h-3`}
                  style={{
                     width: `${
                        percentageUtilized > 100 ? 100 : percentageUtilized
                     }%`,
                  }}
               ></div>
            </div>
         </div>
         <div className="relative z-10">
            <div className="flex flex-col items-center space-y-6">
               {filteredCategories.map((category) => (
                  <Category
                     key={category.categoryId}
                     category={category}
                     handleShowSplitTransactionModal={
                        handleShowSplitTransactionModal
                     }
                     handleShowReduceTransactionModal={
                        handleShowReduceTransactionModal
                     }
                     handleShowRenameTransactionModal={
                        handleShowRenameTransactionModal
                     }
                     handleShowAssignCategoryModal={
                        handleShowAssignCategoryModal
                     }
                     handleShowUpdateAllocationsModal={
                        handleShowUpdateAllocationsModal
                     }
                     handleShowRenameCategoryModal={
                        handleShowRenameCategoryModal
                     }
                  />
               ))}
            </div>
         </div>
      </div>
   );
};

export default CategoryType;
