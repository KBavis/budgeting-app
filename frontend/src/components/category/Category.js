import React, { useContext, useEffect, useState } from "react";
import { useDrop } from "react-dnd";
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
   const { transactions, updateCategory } = useContext(transactionContext);

   //Local State
   const [recentTransactions, setRecentTransactions] = useState([]);

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

   //Set Transactions that are corresponding to current Category
   useEffect(() => {
      if (transactions) {
         const filtered = transactions.filter((transaction) => {
            if (transaction.category && transaction.category.categoryId) {
               return transaction.category.categoryId === category.categoryId;
            }
            return false;
         });
         console.log("New Filtered Transactions");
         console.log(filtered);
         const mostRecent = filtered ? filtered.slice(0, 3) : [];
         setRecentTransactions(mostRecent);
      } else {
         setRecentTransactions([]);
      }
   }, [transactions, category.categoryId]);

   return (
      <div
         ref={drop}
         className={`bg-gray-200 rounded-lg shadow-md p-4 ${
            isOver ? "bg-green-300" : canDrop ? "bg-green-200" : ""
         }`}
      >
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
