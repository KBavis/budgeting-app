import React, { useContext, useState } from "react";
import { useDrag } from "react-dnd";
import transactionContext from "../../context/transaction/transactionContext";
import TransactionDropdown from "../layout/TransactionDropdown";

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
                        handleShowRenameTransactionModal,
                        handleShowAssignCategoryModal,
                     }) => {
   // Local State
   const [dropdownVisible, setDropdownVisible] = useState(false);

   // Global State
   const { deleteTransaction } = useContext(transactionContext);

   // Function to allow the Transaction component to be assigned to corresponding Categories
   const [{ isDragging }, drag] = useDrag(() => ({
      type: "transaction",
      item: { transaction },
      collect: (monitor) => ({
         isDragging: !!monitor.isDragging(),
      }),
   }));

   // Function to Handle Dropdown
   const toggleDropdown = () => {
      setDropdownVisible(!dropdownVisible);
   };

   // Function to Assign a Transaction a Category
   const handleReassignTransaction = () => {
      handleShowAssignCategoryModal(transaction);
   };

   // Function to handle drop down option 'Split Transaction'
   const handleSplitTransaction = () => {
      toggleDropdown();
      handleShowSplitTransactionModal(transaction);
   };

   // Function to handle drop down option 'Reduce Amount'
   const handleReduceTransaction = () => {
      toggleDropdown();
      handleShowReduceTransactionModal(transaction);
   };

   // Function to delete drop down 'Delete Transaction'
   const handleDeleteTransaction = () => {
      toggleDropdown();
      deleteTransaction(transaction.transactionId);
   };

   // Function to handle drop down option 'Rename Transaction'
   const handleRenameTransaction = () => {
      toggleDropdown();
      handleShowRenameTransactionModal(transaction);
   };

   // Round The Transaction Amount
   const roundedAmount = Math.round(transaction.amount);

   // Format Transaction Date
   const formattedDate = new Date(transaction.date).toLocaleDateString("en-US");

   return (
       <div
           ref={drag}
           className={`cursor-pointer bg-indigo-900 rounded-lg shadow-md p-2 flex items-center space-x-2 w-full h-16 relative xs:h-14 xs:p-1 ${
               isDragging ? "opacity-50" : ""
           }`}
       >
          {transaction.logoUrl ? (
              <img
                  src={transaction.logoUrl}
                  alt="Transaction Logo"
                  className="w-10 h-10 rounded-full xs:w-8 xs:h-8"
              />
          ) : (
              <img
                  src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                  className="w-10 h-10 rounded-full xs:w-8 xs:h-8"
              />
          )}
          <div className="text-white text-left flex-1">
             <div className="flex justify-between">
                <p className="text-sm font-bold xs:text-xs">{transaction.name}</p>
                <p className="text-xs font-bold xs:text-xxs">{formattedDate}</p>
             </div>
             <p className="text-xs xl:text-sm xs:text-xxs">${roundedAmount}</p>
          </div>
          {transaction.category && ( // Only display the dropdown if category is not null
              <TransactionDropdown
                  handleDeleteTransaction={handleDeleteTransaction}
                  handleSplitTransaction={handleSplitTransaction}
                  handleReassignTransaction={handleReassignTransaction}
                  handleRenameTransaction={handleRenameTransaction}
                  handleReduceTransaction={handleReduceTransaction}
              />
          )}
       </div>
   );
};

export default Transaction;


