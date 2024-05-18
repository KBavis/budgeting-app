import React, { useContext, useState, useEffect } from "react";
import Category from "../Category";
import transactionContext from "../../../context/transaction/transactionContext";
import categoryContext from "../../../context/category/categoryContext";

/**
 * Component used on Home Page to represent a CategoryType
 *
 * @param categoryType
 * - CategoryType to generate component for
 */
const CategoryType = ({ categoryType }) => {
   // Global State
   const { transactions } = useContext(transactionContext);
   const { categories } = useContext(categoryContext);

   // Local State
   const [filteredCategories, setFilteredCategories] = useState([]);
   const [filteredTransactions, setFilteredTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [totalAmountAllocated, setTotalAmountAllocated] = useState(0);

   // Set Allocated Amount of Budget for each CategoryType
   useEffect(() => {
      setTotalAmountAllocated(categoryType.budgetAmount);
   }, [categoryType]);

   // Filter Categories that are specific to the current CategoryType
   useEffect(() => {
      const filtered = categories.filter(
         (category) =>
            category.categoryType.categoryTypeId === categoryType.categoryTypeId
      );
      setFilteredCategories(filtered);
   }, [categories, categoryType.categoryTypeId]);

   // Filter out transactions based on the corresponding Category
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

   // Calculate total amount spent for each Transaction
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

   return (
      <div className="relative bg-white rounded-lg shadow-md p-4 w-1/3 mx-2">
         <div
            className="absolute bottom-0 left-0 right-0 bg-indigo-600 transition-height duration-500 ease-in-out"
            style={{ height: `${percentageUtilized}%` }}
         ></div>
         <div className="relative z-10">
            <h3 className="text-2xl text-center text-black font-bold mb-2">
               {categoryType.name}
            </h3>
            <p className="text-black mb-4 text-center font-semibold">
               Spent: ${totalAmountSpent} / ${totalAmountAllocated}
            </p>
            <div className="space-y-2">
               {filteredCategories.map((category) => (
                  <Category key={category.id} category={category} />
               ))}
            </div>
         </div>
      </div>
   );
};

export default CategoryType;
