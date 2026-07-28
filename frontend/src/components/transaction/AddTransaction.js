import React, { useContext, useState } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import AlertContext from "../../context/alert/alertContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";

const AddTransaction = ({ onClose }) => {
   // Local State
   const [transactionName, setTransactionName] = useState("");
   const [transactionAmount, setTransactionAmount] = useState("");
   const [transactionDate, setTransactionDate] = useState("");

   // Global State
   const { addTransaction } = useContext(transactionContext);
   const { setAlert } = useContext(AlertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   // Compute local today's date in YYYY-MM-DD (avoiding UTC timezone offset issues)
   const getLocalTodayStr = () => {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
   };
   const todayStr = getLocalTodayStr();

   const isFutureDate = Boolean(transactionDate && transactionDate > todayStr);

   const handleSubmit = (e) => {
      e.preventDefault();
      if (!transactionName || !transactionAmount || !transactionDate) {
         setAlert("Please fill in all fields.", "danger");
         return;
      }
      if (transactionDate > todayStr) {
         setAlert("Transaction date cannot be in the future. Please select today or a past date.", "danger");
         return;
      }
      const transactionDto = {
         updatedName: transactionName,
         updatedAmount: parseFloat(transactionAmount),
         date: transactionDate,
      };
      addTransaction(transactionDto);
      onClose();
   };

   return (
      <Modal isOpen={true} onClose={onClose} title="Add Transaction" size="md">
         <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
               Please fill out the following information to create a transaction independent of any financial institution.
            </p>
            <p className={`text-xs p-3 rounded-xl border font-semibold ${
               isDark
                  ? "text-amber-300 bg-amber-500/10 border-amber-500/20"
                  : "text-amber-900 bg-amber-50 border-amber-200"
            }`}>
               💡 Note: You must assign this added Transaction to an associated Category prior to logging out!
            </p>

            <div className="flex flex-col gap-1">
               <label htmlFor="transactionName" className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Transaction Name
               </label>
               <input
                  type="text"
                  id="transactionName"
                  value={transactionName}
                  onChange={(e) => setTransactionName(e.target.value)}
                  placeholder="e.g. Coffee shop"
                  className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all ${
                     isDark
                        ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                        : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400 focus:bg-white"
                  }`}
                  required
               />
            </div>

            <div className="flex flex-col gap-1">
               <label htmlFor="transactionAmount" className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Transaction Amount
               </label>
               <div className="relative flex items-center">
                  <span className={`absolute left-3 text-sm font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>$</span>
                  <input
                     type="number"
                     step="0.01"
                     id="transactionAmount"
                     value={transactionAmount}
                     onChange={(e) => setTransactionAmount(e.target.value)}
                     placeholder="0.00"
                     className={`w-full pl-7 pr-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all ${
                        isDark
                           ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                           : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400 focus:bg-white"
                     }`}
                     required
                  />
               </div>
            </div>

            <div className="flex flex-col gap-1">
               <label htmlFor="transactionDate" className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                  Transaction Date
               </label>
               <input
                  type="date"
                  id="transactionDate"
                  value={transactionDate}
                  max={todayStr}
                  style={{ colorScheme: isDark ? "dark" : "light" }}
                  onChange={(e) => setTransactionDate(e.target.value)}
                  className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 transition-all ${
                     isFutureDate
                        ? "bg-red-500/10 border-2 border-red-500 text-red-500 focus:ring-red-500/50"
                        : isDark
                           ? "bg-slate-900 border border-slate-700 text-slate-100 focus:ring-indigo-500/50"
                           : "bg-slate-50 border border-slate-300 text-slate-900 focus:bg-white focus:ring-indigo-500/50"
                  }`}
                  required
               />
               {isFutureDate ? (
                  <p className="text-xs font-bold text-red-500 mt-1 flex items-center gap-1">
                     ⚠️ Date cannot be in the future. Please select today ({todayStr}) or an earlier date.
                  </p>
               ) : (
                  <p className={`text-[10px] ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                     Date must be on or before today ({todayStr})
                  </p>
               )}
            </div>

            <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
               <button
                  type="button"
                  onClick={onClose}
                  className={`px-4 py-2 text-sm font-medium rounded-lg transition-colors ${
                     isDark
                        ? "text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700"
                        : "text-slate-700 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 border border-slate-200"
                  }`}
               >
                  Cancel
               </button>
               <button
                  type="submit"
                  className="px-5 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg transition-colors shadow-md"
               >
                  Confirm
               </button>
            </div>
         </form>
      </Modal>
   );
};

export default AddTransaction;