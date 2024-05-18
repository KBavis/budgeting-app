import React from "react";

const Transaction = ({ transaction }) => {
   return (
      <div className="bg-white rounded-full shadow-md p-2 flex items-center space-x-2">
         {transaction.logo && (
            <img
               src={transaction.logo}
               alt="Transaction Logo"
               className="w-6 h-6"
            />
         )}
         <div>
            <p className="text-sm font-bold">{transaction.name}</p>
            <p className="text-sm">${transaction.amount}</p>
         </div>
      </div>
   );
};

export default Transaction;
