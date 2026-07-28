import React, { useContext, useState } from "react";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";

const RenameTransaction = ({ onClose, transaction }) => {
   const { setAlert } = useContext(AlertContext);
   const { renameTransaction } = useContext(transactionContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const [newName, setNewName] = useState(transaction?.name || "");

   const handleInputChange = (event) => {
      setNewName(event.target.value);
   };

   const onConfirm = () => {
      if (newName.trim() === "") {
         setAlert("Please enter a valid name", "danger");
         return;
      }

      renameTransaction(transaction.transactionId, newName.trim());
      setAlert("Transaction renamed successfully", "success");
      onClose();
   };

   return (
      <Modal isOpen={true} onClose={onClose} title="Rename Transaction" size="md">
         <div className="flex flex-col gap-4">
            <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               To rename the transaction, please enter the new name below.
            </p>

            <div className={`flex items-center gap-3 p-3.5 border rounded-xl ${
               isDark ? "bg-slate-800/80 border-slate-700/50" : "bg-slate-50 border-slate-200"
            }`}>
               <img
                  src={
                     transaction?.logoUrl ||
                     "https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                  }
                  alt="Transaction Logo"
                  className="w-10 h-10 rounded-full object-cover border border-slate-400/30"
               />
               <div>
                  <p className={`font-bold ${isDark ? "text-slate-100" : "text-slate-900"}`}>{transaction?.name}</p>
                  <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-500"}`}>${transaction?.amount}</p>
               </div>
            </div>

            <div className="flex flex-col gap-1">
               <input
                  type="text"
                  value={newName}
                  onChange={handleInputChange}
                  placeholder="New Name"
                  className={`w-full px-3.5 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                     isDark
                        ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                        : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400 focus:bg-white"
                  }`}
               />
            </div>

            <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
               <button
                  type="button"
                  onClick={onClose}
                  className={`px-4 py-2 text-sm font-semibold rounded-lg transition-colors ${
                     isDark
                        ? "text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700"
                        : "text-slate-700 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 border border-slate-200"
                  }`}
               >
                  Cancel
               </button>
               <button
                  type="button"
                  onClick={onConfirm}
                  className="px-5 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg transition-colors shadow-md"
               >
                  Confirm
               </button>
            </div>
         </div>
      </Modal>
   );
};

export default RenameTransaction;
