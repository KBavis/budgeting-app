// CategoryType.js

import React, { useContext, useState, useEffect } from "react";
import Category from "../Category";
import transactionContext from "../../../context/transaction/transactionContext";
import categoryContext from "../../../context/category/categoryContext";

const CategoryType = ({ categoryType }) => {
   const { transactions } = useContext(transactionContext);
   const { categories } = useContext(categoryContext);

   const [filteredCategories, setFilteredCategories] = useState([]);
   const [filteredTransactions, setFilteredTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [totalAmountAllocated, setTotalAmountAllocated] = useState(0);

   useEffect(() => {
      setTotalAmountAllocated(categoryType.budgetAmount);
   }, [categoryType]);

   useEffect(() => {
      const filtered = categories.filter(
         (category) =>
            category.categoryType.categoryTypeId === categoryType.categoryTypeId
      );
      setFilteredCategories(filtered);
   }, [categories, categoryType.categoryTypeId]);

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

   const percentageUtilized =
      totalAmountAllocated > 0
         ? Math.min(
              Math.round((totalAmountSpent / totalAmountAllocated) * 100),
              100
           )
         : 0;

   const isBackgroundFilled = percentageUtilized >= 95;

   return (
      <div className="relative bg-white rounded-lg shadow-md p-4 w-1/3 mx-2">
         <div
            className="absolute bottom-0 left-0 right-0 bg-indigo-900 transition-height duration-500 ease-in-out"
            style={{ height: `${percentageUtilized}%` }}
         ></div>
         <div className="relative z-10">
            <h3
               className={`text-2xl text-center font-bold mb-2 ${
                  isBackgroundFilled ? "text-white" : "text-black"
               }`}
            >
               {categoryType.name}
            </h3>
            <p
               className={`mb-4 text-center font-semibold ${
                  isBackgroundFilled ? "text-white" : "text-black"
               }`}
            >
               Spent: ${totalAmountSpent} / ${totalAmountAllocated}
            </p>
            <div className="flex flex-col items-center space-y-6">
               {filteredCategories.map((category) => (
                  <Category key={category.categoryId} category={category} />
               ))}
            </div>
         </div>
      </div>
   );
};

export default CategoryType;
