import React, { useContext, useState, useEffect } from "react";
import Category from "../Category";
import transactionContext from "../../../context/transaction/transactionContext";
import categoryContext from "../../../context/category/categoryContext";

const CategoryType = ({ categoryType }) => {
   const { transactions } = useContext(transactionContext);
   const { categories } = useContext(categoryContext);
   const [filteredCategories, setFilteredCategories] = useState([]);

   useEffect(() => {
      const filtered = categories.filter(
         (category) =>
            category.categoryType.categoryTypeId === categoryType.categoryTypeId
      );
      setFilteredCategories(filtered);
   }, [categories]);

   const totalSpent = filteredCategories.reduce(
      (sum, category) =>
         sum +
         transactions
            .filter((transaction) => transaction.categoryId === category.id)
            .reduce((total, transaction) => total + transaction.amount, 0),
      0
   );

   const totalAllocated = categoryType.allocatedAmount;
   const percentageSpent = (totalSpent / totalAllocated) * 100;

   return (
      <div className="bg-white rounded-lg shadow-md p-4 w-1/3 mx-2">
         <h3 className="text-xl font-bold mb-2">{categoryType.name}</h3>
         <p className="text-gray-600 mb-4">
            Spent: ${totalSpent} / ${totalAllocated}
         </p>
         <div className="bg-gray-200 rounded-full h-4 mb-4">
            <div
               className="bg-blue-500 h-4 rounded-full"
               style={{ width: `${percentageSpent}%` }}
            ></div>
         </div>
         <div className="space-y-2">
            {filteredCategories.map((category) => (
               <Category key={category.id} category={category} />
            ))}
         </div>
      </div>
   );
};

export default CategoryType;
