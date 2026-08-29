import React, { useContext, useState } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import TransactionActionModal from "./TransactionActionModal";
import ConfirmationModal from "../layout/ConfirmationModal";
import { ThemeContext } from "../../context/theme/ThemeContext";
import { FaSlidersH } from "react-icons/fa";

const DetailedCategoryTransaction = ({
   transaction,
   handleShowSplitTransactionModal,
   handleShowReduceTransactionModal,
   handleShowRenameTransactionModal,
   handleShowAssignCategoryModal,
}) => {
   const roundedAmount = Math.round(transaction.amount);
   const { deleteTransaction } = useContext(transactionContext);
   const { theme } = useContext(ThemeContext);
   const isDark = theme === "dark";

   const [showActionModal, setShowActionModal] = useState(false);
   const [showConfirmDelete, setShowConfirmDelete] = useState(false);

   const handleDeleteTransaction = () => {
      deleteTransaction(transaction.transactionId);
      setShowConfirmDelete(false);
   };

   const formattedDate = new Date(transaction.date).toLocaleDateString("en-US");

   return (
      <>
         <div
            onClick={() => setShowActionModal(true)}
            className={`rounded-xl p-3 shadow-md flex items-center justify-between transition-all duration-200 border cursor-pointer group ${
               isDark
                  ? "bg-slate-800/90 border-slate-700/80 text-slate-100 hover:bg-slate-800 hover:border-slate-600"
                  : "bg-slate-50 border-slate-200 text-slate-800 hover:bg-white shadow-sm hover:border-slate-300"
            }`}
            title="Click to view transaction actions"
         >
            <div className="flex items-center space-x-3 flex-grow overflow-hidden pr-2">
               {transaction.logoUrl ? (
                  <img
                     src={transaction.logoUrl}
                     alt="Transaction Logo"
                     className="w-8 h-8 rounded-full flex-shrink-0 object-cover border border-slate-400/30"
                  />
               ) : (
                  <img
                     src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                     className="w-8 h-8 rounded-full flex-shrink-0 object-cover border border-slate-400/30"
                     alt="Default Avatar"
                  />
               )}
               <div className="flex-grow min-w-0">
                  <p className={`text-sm font-bold truncate mb-0.5 ${isDark ? "text-white group-hover:text-indigo-400" : "text-slate-900 group-hover:text-indigo-600"} transition-colors`}>
                     {transaction.name}
                  </p>
                  <div className="flex items-center justify-between text-xs">
                     <span className={`font-bold ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                        ${roundedAmount}
                     </span>
                     <span className={`text-[11px] ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        {formattedDate}
                     </span>
                  </div>
               </div>
            </div>

            {/* Quick Options Button */}
            <div className="flex items-center flex-shrink-0 pl-1">
               <button
                  type="button"
                  onClick={(e) => {
                     e.stopPropagation();
                     setShowActionModal(true);
                  }}
                  className={`p-1.5 rounded-lg border transition-colors ${
                     isDark
                        ? "text-slate-400 hover:text-white bg-slate-700/50 border-slate-600"
                        : "text-slate-500 hover:text-slate-900 bg-white border-slate-200"
                  }`}
                  title="Transaction Options"
               >
                  <FaSlidersH size={12} />
               </button>
            </div>
         </div>

         {/* Transaction Action Popover Modal */}
         <TransactionActionModal
            isOpen={showActionModal}
            onClose={() => setShowActionModal(false)}
            transaction={transaction}
            handleRename={handleShowRenameTransactionModal}
            handleReassign={handleShowAssignCategoryModal}
            handleSplit={handleShowSplitTransactionModal}
            handleReduce={handleShowReduceTransactionModal}
            handleDelete={() => setShowConfirmDelete(true)}
         />

         {/* Delete Confirmation Modal */}
         {showConfirmDelete && (
            <ConfirmationModal
               question={`Are you sure you want to delete the transaction "${transaction.name}"?`}
               onConfirm={handleDeleteTransaction}
               onClose={() => setShowConfirmDelete(false)}
            />
         )}
      </>
   );
};

export default DetailedCategoryTransaction;
