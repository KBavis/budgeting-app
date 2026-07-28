import React, { useContext, useState } from "react";
import { useDrag } from "react-dnd";
import transactionContext from "../../context/transaction/transactionContext";
import TransactionActionModal from "./TransactionActionModal";
import ConfirmationModal from "../layout/ConfirmationModal";
import { ThemeContext } from "../../context/theme/ThemeContext";
import { FaSlidersH } from "react-icons/fa";

/**
 * Transaction component on Unassigned Toolbar with drag-and-drop
 * and clean action modal (replacing clunky ellipsis dropdown).
 */
const Transaction = ({
   transaction,
   handleShowSplitTransactionModal,
   handleShowReduceTransactionModal,
   handleShowRenameTransactionModal,
   handleShowAssignCategoryModal,
}) => {
   const [showActionModal, setShowActionModal] = useState(false);
   const [showConfirmDelete, setShowConfirmDelete] = useState(false);

   const { deleteTransaction } = useContext(transactionContext);
   const { theme } = useContext(ThemeContext);
   const isDark = theme === "dark";

   const [{ isDragging }, drag] = useDrag(() => ({
      type: "transaction",
      item: { transaction },
      collect: (monitor) => ({
         isDragging: !!monitor.isDragging(),
      }),
   }));

   const handleDeleteTransaction = () => {
      deleteTransaction(transaction.transactionId);
      setShowConfirmDelete(false);
   };

   const roundedAmount = Math.round(transaction.amount);
   const formattedDate = new Date(transaction.date).toLocaleDateString("en-US");

   return (
      <>
         <div
            ref={drag}
            onClick={() => setShowActionModal(true)}
            className={`cursor-pointer rounded-xl shadow-md p-2 flex items-center space-x-2.5 w-full h-16 relative transition-all duration-200 border ${
               isDragging ? "opacity-40" : ""
            } ${
               isDark
                  ? "bg-slate-800/90 border-slate-700/80 text-slate-100 hover:bg-slate-800"
                  : "bg-white border-slate-200 text-slate-800 hover:bg-slate-50 shadow-sm"
            }`}
         >
            {transaction.logoUrl ? (
               <img
                  src={transaction.logoUrl}
                  alt="Transaction Logo"
                  className="w-9 h-9 rounded-full flex-shrink-0 object-cover border border-slate-400/30"
               />
            ) : (
               <img
                  src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                  className="w-9 h-9 rounded-full flex-shrink-0 object-cover border border-slate-400/30"
                  alt="Default Avatar"
               />
            )}
            <div className="flex-1 min-w-0 text-left">
               <div className="flex justify-between items-center mb-0.5">
                  <p className={`text-xs font-bold truncate ${isDark ? "text-white" : "text-slate-900"}`}>
                     {transaction.name}
                  </p>
                  <p className={`text-[10px] font-semibold flex-shrink-0 ml-2 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                     {formattedDate}
                  </p>
               </div>
               <p className={`text-xs font-black ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                  ${roundedAmount}
               </p>
            </div>
            
            <button
               type="button"
               onClick={(e) => {
                  e.stopPropagation();
                  setShowActionModal(true);
               }}
               className={`p-1.5 rounded-lg border transition-colors flex-shrink-0 ml-auto ${
                  isDark
                     ? "text-slate-400 hover:text-white bg-slate-700/50 border-slate-600"
                     : "text-slate-500 hover:text-slate-900 bg-slate-100 border-slate-200"
               }`}
               title="Transaction Actions"
            >
               <FaSlidersH size={12} />
            </button>
         </div>

         {/* Action Modal */}
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

         {/* Confirmation Delete Modal */}
         {showConfirmDelete && (
            <ConfirmationModal
               question={`Are you sure you want to delete transaction "${transaction.name}"?`}
               onConfirm={handleDeleteTransaction}
               onClose={() => setShowConfirmDelete(false)}
            />
         )}
      </>
   );
};

export default Transaction;
