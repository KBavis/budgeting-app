import React, { useContext } from "react";
import Transaction from "../transaction/Transaction";
import transactionContext from "../../context/transaction/transactionContext";

const Category = ({ category }) => {
   const { transactions } = useContext(transactionContext);

   const filteredTransactions = transactions.filter(
      (transaction) => transaction.categoryId === category.id
   );

   const recentTransactions = filteredTransactions.slice(0, 3);

   return (
      <div className="bg-gray-100 rounded-lg shadow-md p-2">
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
