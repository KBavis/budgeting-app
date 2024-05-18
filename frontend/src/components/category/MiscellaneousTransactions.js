import React, { useContext, useEffect, useState } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import Transaction from "../transaction/Transaction";

/**
 *  Component to store all Miscallenous Transactions that are to be assigned by User
 */
const MiscellaneousTransactions = () => {
   const [miscTransactions, setMiscTransactions] = useState([]);
   const { transactions } = useContext(transactionContext);

   //Filter out transaction that have a NULL Category currently Set
   useEffect(() => {
      const miscellaneousTransactions = transactions.filter(
         (transaction) => transaction.category === null
      );
      setMiscTransactions(miscellaneousTransactions);
   }, [transactions]);

   return (
      <div className="bg-white rounded-lg shadow-md p-4 w-full mt-4 text-center">
         <h3 className="text-xl font-bold mb-2">Miscellaneous Transactions</h3>
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
