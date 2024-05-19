// Category.js

import React, { useContext, useEffect, useState } from "react";
import { useDrop } from "react-dnd";
import Transaction from "../transaction/Transaction";
import transactionContext from "../../context/transaction/transactionContext";

const Category = ({ category }) => {
   const { transactions, updateCategory } = useContext(transactionContext);

   const [recentTransactions, setRecentTransactions] = useState([]);

   const [{ canDrop, isOver }, drop] = useDrop(() => ({
      accept: "transaction",
      drop: (item) => {
         console.log("Dropped item:", item);
         updateCategory(item.transaction.transactionId, category.categoryId);
      },
      collect: (monitor) => ({
         isOver: !!monitor.isOver(),
         canDrop: !!monitor.canDrop(),
      }),
   }));

   useEffect(() => {
      if (transactions) {
         const filtered = transactions.filter((transaction) => {
            return (
               transaction.category &&
               transaction.category.categoryId === category.categoryId
            );
         });
         const mostRecent = filtered ? filtered.slice(0, 3) : [];
         console.log(
            `Most Recent Transactions for Category '${category.name}'`
         );
         console.log(mostRecent);
         setRecentTransactions(mostRecent);
      } else {
         setRecentTransactions([]);
      }
   }, [transactions, category.categoryId]);

   return (
      <div
         ref={drop}
         className={`bg-gray-200 rounded-lg shadow-md p-4${
            isOver ? " bg-indigo-200" : canDrop ? " bg-indigo-100" : ""
         }`}
         style={{ width: "90%" }}
      >
         <h4 className="text-lg font-bold mb-2">{category.name}</h4>
         <div className="space-y-2">
            {recentTransactions?.map((transaction) => (
               <Transaction
                  key={transaction.transactionId}
                  transaction={transaction}
               />
            ))}
         </div>
      </div>
   );
};

export default Category;
