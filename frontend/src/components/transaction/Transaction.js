import React, { useContext, useState } from "react";
import { useDrag } from "react-dnd";
import { FaEllipsisV } from "react-icons/fa"; // Importing icon
import transactionContext from "../../context/transaction/transactionContext";

/**
 *
 * @param transaction
 *          - Transaction to generate component for
 * @param handleShowSplitTransactionModal
 *          - Function passed down through props to handle the showing of Split Transaction modal
 * @param handleShowReduceTransactionModal
 *          - Function passed down through props to handle the showing of the Reduce Transaction modal
 * @returns
 */
const Transaction = ({
   transaction,
   handleShowSplitTransactionModal,
   handleShowReduceTransactionModal,
}) => {
   //Local State
   const [dropdownVisible, setDropdownVisible] = useState(false);

   //Global State
   const { deleteTransaction } = useContext(transactionContext);

   //Function to allow the Transaction component to be assigned to corresponding Cateogires
   const [{ isDragging }, drag] = useDrag(() => ({
      type: "transaction",
      item: { transaction },
      collect: (monitor) => ({
         isDragging: !!monitor.isDragging(),
      }),
   }));

   //Function to Handle
   const toggleDropdown = () => {
      setDropdownVisible(!dropdownVisible);
   };

   //Function to handle drop down option 'Split Transaction'
   const handleSplitTransaction = () => {
      toggleDropdown();
      handleShowSplitTransactionModal(transaction);
   };

   //Function to handle drop down option 'Reduce Amount'
   const handleReduceTransaction = () => {
      toggleDropdown();
      handleShowReduceTransactionModal(transaction);
   };

   //Function to delete drop down 'Delete Transaction'
   const handleDeleteTransaction = () => {
      toggleDropdown();
      deleteTransaction(transaction.transactionId);
   };

   //Round The Transaction Amount
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
               {/* Drop Down options to edit Transaction. TODO: Update this to be Dropdown component */}
               {dropdownVisible && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-[200]">
                     <button
                        onClick={handleSplitTransaction}
                        className="font-bold border-[1px] border-black block w-full px-2 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                     >
                        Split Transaction
                     </button>
                     <button
                        onClick={handleReduceTransaction}
                        className="font-bold block border-[1px] border-black w-full px-2 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                     >
                        Reduce Amount
                     </button>
                     <button
                        onClick={handleDeleteTransaction}
                        className="font-bold block border-[1px] border-black w-full px-2 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                     >
                        Delete Transaction
                     </button>
                  </div>
               )}
            </div>
         )}
      </div>
   );
};

export default Transaction;
