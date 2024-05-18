import React, { useContext, useEffect, useState } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import Transaction from "../transaction/Transaction";
import { useDrop } from "react-dnd";

/**
 * Component to store all Miscellaneous Transactions that are to be assigned by User
 */
const MiscellaneousTransactions = () => {
   const [miscTransactions, setMiscTransactions] = useState([]);
   const { transactions, updateCategory } = useContext(transactionContext);

   const [{ canDrop, isOver }, drop] = useDrop(() => ({
      accept: "transaction",
      drop: (item) => {
         updateCategory(item.transaction.transactionId, null);
      },
      collect: (monitor) => ({
         isOver: !!monitor.isOver(),
         canDrop: !!monitor.canDrop(),
      }),
   }));

   //Filter out transaction that have a NULL Category currently Set
   useEffect(() => {
      const miscellaneousTransactions = transactions.filter(
         (transaction) => transaction.category === null
      );
      setMiscTransactions(miscellaneousTransactions);
   }, [transactions]);

   return (
      <div
         ref={drop}
         className={`bg-white rounded-lg shadow-md p-4 w-full mt-4 text-center ${
            isOver ? "bg-green-200" : canDrop ? "bg-green-100" : ""
         }`}
      >
         <h3 className="text-xl font-bold mb-1">Miscellaneous Transactions</h3>
         {miscTransactions.length > 0 && (
            <p className="mb-10">
               Please drag and drop the following Transactions to their
               respective Category.
            </p>
         )}
         <div className="grid grid-cols-10 gap-4">
            {miscTransactions.map((transaction) => (
               <div key={transaction.id} className="w-full">
                  <Transaction transaction={transaction} />
               </div>
            ))}
         </div>
      </div>
   );
};

export default MiscellaneousTransactions;
