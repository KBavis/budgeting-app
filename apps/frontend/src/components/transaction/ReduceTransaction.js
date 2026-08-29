import React, { useContext, useState } from "react";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";

const ReduceTransaction = ({ onClose, transaction }) => {
   const { setAlert } = useContext(AlertContext);
   const { reduceTransactionAmount } = useContext(transactionContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";
   const [reducedAmount, setReducedAmount] = useState("");

   const handleInputChange = (event) => {
      setReducedAmount(event.target.value);
   };

   const onConfirm = () => {
      const reducedAmountFloat = parseFloat(reducedAmount);

      if (isNaN(reducedAmountFloat) || reducedAmountFloat <= 0) {
         setAlert("Please enter a valid amount.", "danger");
         return;
      }

      if (reducedAmountFloat >= parseFloat(transaction.amount)) {
         setAlert(
            "The reduced amount must be less than the original transaction amount.",
            "danger"
         );
         return;
      }

      reduceTransactionAmount(transaction.transactionId, reducedAmountFloat);
      setAlert(`Amount reduced to $${reducedAmountFloat.toFixed(2)}`, "success");
      onClose();
   };

   return (
      <Modal isOpen={true} onClose={onClose} title="Reduce Transaction Amount" size="md">
         <div className="flex flex-col gap-4">
            <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               To reduce the transaction amount, please enter the new amount below.
               Ensure that the new amount is less than the original amount (${transaction?.amount}).
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

            <div className={`p-4 border rounded-xl flex flex-col items-center gap-3 ${
               isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-slate-200"
            }`}>
               <div className="relative flex items-center w-full max-w-xs">
                  <span className={`absolute left-3.5 text-sm font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>$</span>
                  <input
                     type="number"
                     step="0.01"
                     value={reducedAmount}
                     onChange={handleInputChange}
                     placeholder="New Amount"
                     className={`w-full pl-8 pr-3.5 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                        isDark
                           ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                           : "bg-white border border-slate-300 text-slate-900 placeholder-slate-400"
                     }`}
                  />
               </div>
               <div className="flex gap-2 justify-center">
                  {[10, 25, 50, 75].map((pct) => (
                     <button
                        key={pct}
                        type="button"
                        onClick={() => {
                           const newAmt = (transaction.amount * (1 - pct / 100)).toFixed(2);
                           setReducedAmount(newAmt);
                        }}
                        className={`px-3 py-1 text-xs font-semibold rounded-lg border transition-all ${
                           isDark
                              ? "bg-slate-800 border-slate-700 text-slate-300 hover:bg-brand-600 hover:text-white"
                              : "bg-white border-slate-200 text-slate-700 hover:bg-brand-600 hover:text-white"
                        }`}
                     >
                        -{pct}%
                     </button>
                  ))}
               </div>
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

export default ReduceTransaction;
