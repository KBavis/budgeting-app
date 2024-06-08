import React, { useState } from "react";
import { useDrag } from "react-dnd";
import { FaEllipsisV } from "react-icons/fa"; // Importing icon
import SplitTransactionModal from "../transaction/SplitTransaction";

const Transaction = ({ transaction, handleShowModal }) => {
   const [{ isDragging }, drag] = useDrag(() => ({
      type: "transaction",
      item: { transaction },
      collect: (monitor) => ({
         isDragging: !!monitor.isDragging(),
      }),
   }));

   const [dropdownVisible, setDropdownVisible] = useState(false);
   const [showModal, setShowModal] = useState(false);

   const toggleDropdown = () => {
      setDropdownVisible(!dropdownVisible);
   };

   const handleSplitTransaction = () => {
      toggleDropdown();
      handleShowModal(transaction);
   };

   const roundedAmount = Math.round(transaction.amount);

   return (
      <div
         ref={drag}
         className={`cursor-pointer bg-indigo-900 rounded-lg shadow-md p-2 flex items-center space-x-2 w-full h-12 relative ${
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
            <img
               src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
               className="w-8 h-8 rounded-full"
            />
         )}
         <div className="text-white text-left flex-1">
            <p className="text-xs xl:text-sm font-bold">{transaction.name}</p>
            <p className="text-xs xl:text-sm">${roundedAmount}</p>
         </div>
         {transaction.category && ( // Only display the dropdown if category is not null
            <div className="relative ">
               <button
                  onClick={toggleDropdown}
                  className="text-white focus:outline-none"
               >
                  <FaEllipsisV />
               </button>
               {dropdownVisible && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-[200]">
                     <button
                        onClick={handleSplitTransaction}
                        className="font-bold border-[1px] border-black block w-full px-2 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                     >
                        Split Transaction
                     </button>
                     <button
                        onClick={handleSplitTransaction}
                        className="font-bold block border-[1px] border-black w-full px-2 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                     >
                        Reduce Amount
                     </button>
                  </div>
               )}
            </div>
         )}
         {showModal && (
            <SplitTransactionModal
               onClose={() => setShowModal(false)}
               onConfirm={() => setShowModal(false)}
            />
         )}
      </div>
   );
};

export default Transaction;
