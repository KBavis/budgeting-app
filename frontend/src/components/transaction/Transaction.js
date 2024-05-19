import React from "react";
import { useDrag } from "react-dnd";

/**
 * Component used to represent an Individual Transaction
 *
 * @param transaction
 * - Transaction to generate Component for
 */
const Transaction = ({ transaction }) => {
   const [{ isDragging }, drag] = useDrag(() => ({
      type: "transaction",
      item: { transaction },
      collect: (monitor) => ({
         isDragging: !!monitor.isDragging(),
      }),
   }));

   const roundedAmount = Math.round(transaction.amount);

   return (
      <div
         ref={drag}
         className={`cursor-pointer bg-indigo-900 rounded-lg shadow-md p-2 flex items-center space-x-2 ${
            isDragging ? "opacity-50" : ""
         }`}
      >
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
         <div className="text-white text-left">
            <p className="text-sm font-bold">{transaction.name}</p>
            <p className="text-sm">${roundedAmount}</p>
         </div>
      </div>
   );
};

export default Transaction;
