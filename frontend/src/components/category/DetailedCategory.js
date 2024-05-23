import React, { useState, useEffect, useContext } from "react";
import categoryContext from "../../context/category/categoryContext";
import transactionContext from "../../context/transaction/transactionContext";

/**
 * Component to store each
 *
 * @param category
 */
const DetailedCategory = ({ category }) => {
   //Local State
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [budgetUsage, setBudgetUsage] = useState(0);

   //Global State
   const { transactions } = useContext(transactionContext);
   useEffect(() => {
      if (transactions) {
         //Filter Transactions corresponding to current Category
         const filtered = transactions.filter(
            (transaction) =>
               transaction.category &&
               transaction.category.categoryId === category.categoryId
         );

         //Sum total amount of Transactions corresponding to entitiy
         const sum = filtered.reduce((acc, transaction) => {
            return acc + transaction.amount;
         }, 0);

         //Set Total Amount & Budget Usage Percentage
         setTotalAmountSpent(sum.toFixed(0));
         setBudgetUsage((sum / category.budgetAmount) * 100);
      }
   }, [transactions, category.categoryId]);

   //Function to determine progress bar for our Category entitiy
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

   useEffect(() => {}, [category.categoryId]);

   return (
      <div className="bg-white rounded-lg shadow-md p-4 w-1/3 mx-2">
         <h3 className="text-2xl font-bold mb-2">{category.name}</h3>
         <div className="w-full bg-gray-300 rounded-full h-4 mb-4">
            <div
               className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()}`}
               style={{ width: `${budgetUsage > 100 ? 100 : budgetUsage}%` }}
            ></div>
         </div>
      </div>
   );
};

export default DetailedCategory;
