import React from "react";

/**
 * Component used to represent an Individual Transaction
 *
 * @param transaction
 * - Transaction to generate Component for
 */
const Transaction = ({ transaction }) => {
   return (
      <div className="bg-gradient-to-br from-gray-800 to-indigo-900 rounded-lg shadow-md p-2 flex items-center space-x-2">
         {transaction.logoUrl ? (
            <img
               src={transaction.logoUrl}
               alt="Transaction Logo"
               className="w-8 h-8 rounded-full"
            />
         ) : (
            //TODO: use API in backend to resolve this from ever occuring
            <img
               src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
               className="w-8 h-8 rounded-full"
            />
         )}
         <div className="text-white">
            <p className="text-sm font-bold">{transaction.name}</p>
            <p className="text-sm">${transaction.amount}</p>
         </div>
      </div>
   );
};

export default Transaction;
