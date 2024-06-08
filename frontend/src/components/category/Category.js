import React, { useContext, useEffect, useState } from "react";
import { useDrop } from "react-dnd";
import Transaction from "../transaction/Transaction";
import transactionContext from "../../context/transaction/transactionContext";

/**
 * Component to store all relevant information for Category entitiy on Home Page
 *
 * @param category
 *       - Category entitiy to generate component for
 * @returns
 */
const Category = ({ category, handleShowModal }) => {
   //Global State
   const { transactions, updateCategory } = useContext(transactionContext);

   //Local State
   const [recentTransactions, setRecentTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [budgetUsage, setBudgetUsage] = useState(0);
   const [budgetAllocation, setBudgetAllocation] = useState(0);

   //Functionality to allow a Transaction to be assigned to a Category
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
         //Filter Transactions corresponding to current Category
         const filtered = transactions.filter(
            (transaction) =>
               transaction.category &&
               transaction.category.categoryId === category.categoryId
         );
         //Set three most recent Transactions for Cateogry
         const mostRecent = filtered ? filtered.slice(0, 3) : [];
         setRecentTransactions(mostRecent);

         //Sum total amount of Transactions corresponding to entitiy
         const sum = filtered.reduce((acc, transaction) => {
            return acc + transaction.amount;
         }, 0);

         //Set Total Amount & Budget Usage Percentage
         setTotalAmountSpent(sum.toFixed(0));
         setBudgetUsage((sum / category.budgetAmount) * 100);
      } else {
         setRecentTransactions([]);
      }
   }, [transactions, category.categoryId]);

   //Set Budget Allocation when Category component initally loaded
   useEffect(() => {
      setBudgetAllocation(category.budgetAmount.toFixed(0));
   }, [category.categoryId]);

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

   return (
      <div
         ref={drop}
         className={`bg-gray-200 rounded-lg shadow-md p-4 ${
            isOver ? " bg-indigo-200" : canDrop ? " bg-indigo-100" : ""
         }`}
         style={{ width: "90%" }}
      >
         <h4 className="text-xl font-bold mb-2 text-gray-800">
            {category.name}
         </h4>
         <div className="mb-2 text-sm font-semibold text-gray-600">
            <p>Allocated Budget: ${budgetAllocation}</p>
            <p>Total Spent: ${totalAmountSpent}</p>
         </div>
         <div className="w-full bg-gray-300 rounded-full h-4 mb-4">
            <div
               className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()}`}
               style={{ width: `${budgetUsage > 100 ? 100 : budgetUsage}%` }}
            ></div>
         </div>
         <div className="space-y-2">
            {recentTransactions?.map((transaction) => (
               <Transaction
                  key={transaction.transactionId}
                  transaction={transaction}
                  handleShowModal={handleShowModal}
               />
            ))}
         </div>
      </div>
   );
};

export default Category;
