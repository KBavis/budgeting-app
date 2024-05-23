import React from "react";

/**
 * Component used to represent an Individual Transaction in a DetailedCategory
 *
 * @param transaction
 * - Transaction to generate Component for
 */
const DetailedCategoryTransaction = ({ transaction }) => {
   // Determine rounded amount for Transaction
   const roundedAmount = Math.round(transaction.amount);

   return (
      <div className="bg-indigo-900 rounded-lg shadow-md p-3 flex items-center space-x-2 mb-3 ">
         {transaction.logoUrl ? (
            <img
               src={transaction.logoUrl}
               alt="Transaction Logo"
               className="w-8 h-8 rounded-full"
            />
         ) : (
            <img
               src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
               className="w-8 h-8 rounded-full"
               alt="Default Avatar"
            />
         )}
         <div className="text-white text-left">
            <p className="text-sm font-bold truncate text-wrap">
               {transaction.name}
            </p>
            <p className="text-sm">${roundedAmount}</p>
         </div>
      </div>
   );
};

export default DetailedCategoryTransaction;
