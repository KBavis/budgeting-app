import React, { useContext, useEffect, useState } from "react";
import Transaction from "../transaction/Transaction";
import transactionContext from "../../context/transaction/transactionContext";

/**
 * Component to store information regarding a Category
 *
 * @param category
 *          - Category we are generating Compnent for
 */
const Category = ({ category }) => {
   //Global State
   const { transactions } = useContext(transactionContext);

   //Local State
   const [recentTransactions, setRecentTransactions] = useState([]);

   //Set Transactions that are corresponding to current Category
   useEffect(() => {
      if (transactions) {
         const filtered = transactions.filter((transaction) => {
            if (transaction.category && transaction.category.categoryId) {
               return transaction.category.categoryId === category.categoryId;
            }
            return false;
         });
         const mostRecent = filtered ? filtered.slice(0, 3) : [];
         setRecentTransactions(mostRecent);
      } else {
         setRecentTransactions([]);
      }
   }, [transactions, category]);

   return (
      <div className="bg-gray-100 rounded-lg shadow-md p-5">
         <h4 className="text-lg font-bold mb-2">{category.name}</h4>
         <div className="space-y-2">
            {recentTransactions?.map((transaction) => (
               <Transaction key={transaction.id} transaction={transaction} />
            ))}
         </div>
      </div>
   );
};

export default Category;
