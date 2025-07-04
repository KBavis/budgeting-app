import React, { useContext } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import TransactionDropdown from "../layout/TransactionDropdown";
import "bootstrap/dist/css/bootstrap.min.css";

const DetailedCategoryTransaction = ({
                                        transaction,
                                        handleShowSplitTransactionModal,
                                        handleShowReduceTransactionModal,
                                        handleShowRenameTransactionModal,
                                        handleShowAssignCategoryModal,
                                     }) => {
   const roundedAmount = Math.round(transaction.amount);
   const { deleteTransaction } = useContext(transactionContext);

   const handleDeleteTransaction = () => {
      deleteTransaction(transaction.transactionId);
   };

   const handleSplitTransaction = () => {
      handleShowSplitTransactionModal(transaction);
   };

   const handleReassignTransaction = () => {
      handleShowAssignCategoryModal(transaction);
   };

   const handleRenameTransaction = () => {
      handleShowRenameTransactionModal(transaction);
   };

   const handleReduceTransaction = () => {
      handleShowReduceTransactionModal(transaction);
   };

   // Format Transaction Date
   const formattedDate = new Date(transaction.date).toLocaleDateString("en-US");

   return (
       <div className="bg-indigo-900 rounded-lg p-3 shadow-md flex items-center justify-between mb-3 xs:p-2">
          <div className="flex items-center space-x-2 flex-grow overflow-hidden">
             {transaction.logoUrl ? (
                 <img
                     src={transaction.logoUrl}
                     alt="Transaction Logo"
                     className="w-8 h-8 rounded-full flex-shrink-0 xs:w-6 xs:h-6"
                 />
             ) : (
                 <img
                     src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                     className="w-8 h-8 rounded-full flex-shrink-0 xs:w-6 xs:h-6"
                     alt="Default Avatar"
                 />
             )}
             <div className="text-white text-left flex-grow">
                <p className="text-sm font-bold break-words xs:text-xs">
                   {transaction.name}
                </p>
                <p className="text-sm xs:text-xs">${roundedAmount}</p>
                <p className="text-xs mt-1 xs:text-xxs">{formattedDate}</p>
             </div>
          </div>
          <TransactionDropdown
              handleDeleteTransaction={handleDeleteTransaction}
              handleSplitTransaction={handleSplitTransaction}
              handleReassignTransaction={handleReassignTransaction}
              handleRenameTransaction={handleRenameTransaction}
              handleReduceTransaction={handleReduceTransaction}
          />
       </div>
   );
};

export default DetailedCategoryTransaction;

