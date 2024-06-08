import React, { useContext, useState, useEffect } from "react";
import Category from "../Category";
import transactionContext from "../../../context/transaction/transactionContext";
import categoryContext from "../../../context/category/categoryContext";
import { useNavigate } from "react-router-dom";
import { FaExternalLinkAlt } from "react-icons/fa"; // Importing icon
import SplitTransactionModal from "../../transaction/SplitTransaction";

const CategoryType = ({ categoryType, handleShowModal }) => {
   const { transactions } = useContext(transactionContext);
   const { categories } = useContext(categoryContext);

   const [filteredCategories, setFilteredCategories] = useState([]);
   const [filteredTransactions, setFilteredTransactions] = useState([]);
   const [totalAmountSpent, setTotalAmountSpent] = useState(0);
   const [totalAmountAllocated, setTotalAmountAllocated] = useState(0);

   useEffect(() => {
      setTotalAmountAllocated(Math.round(categoryType.budgetAmount));
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

   const navigate = useNavigate();

   const percentageUtilized =
      totalAmountAllocated > 0
         ? Math.min(
              Math.round((totalAmountSpent / totalAmountAllocated) * 100),
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

   //Function to navigate to CategoryType page
   const handleClick = () => {
      navigate(`/category/type/${categoryType.name.toLowerCase()}`);
   };

   return (
      <div className="relative bg-white rounded-lg shadow-md px-4 pb-4 w-full h-[400px] overflow-y-auto hover:duration-100 xxl:h-[650px]">
         <div className="sticky top-0 z-20 bg-white pb-2">
            <div className="flex justify-between items-center">
               <div></div> {/* Placeholder to balance the flex alignment */}
               <h3 className="text-3xl text-center font-bold mb-2 mt-2 flex-grow">
                  {categoryType.name}
               </h3>
               <button
                  onClick={handleClick}
                  className="text-black font-bold duration-100 hover:scale-1025 hover:cursor-pointer hover:text-indigo-600"
               >
                  <FaExternalLinkAlt size={20} />
               </button>
            </div>
            <p className="mb-4 text-center font-semibold">
               Spent: ${totalAmountSpent} / ${totalAmountAllocated}
            </p>
            <div className="w-full bg-gray-300 rounded-full h-4 mb-4 pb-3">
               <div
                  className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()}`}
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
                     handleShowModal={handleShowModal}
                  />
               ))}
            </div>
         </div>
      </div>
   );
};

export default CategoryType;
