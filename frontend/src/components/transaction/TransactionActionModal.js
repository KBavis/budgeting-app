import React, { useContext, useState, useEffect } from "react";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import categoryContext from "../../context/category/categoryContext";
import { FaPen, FaTags, FaCut, FaCompressAlt, FaTrashAlt, FaArrowLeft, FaPlus, FaTimes } from "react-icons/fa";

/**
 * TransactionActionModal: Unified Multi-Step Action Hub
 * Step 1: Main Menu of 5 actions
 * Step 2: Interactive Action Form (Rename, Assign, Split, Reduce, Delete) with "← Back to Options"
 * Full Light/Dark mode support.
 */
const TransactionActionModal = ({
   isOpen,
   onClose,
   transaction,
   handleRename,
   handleReassign,
   handleSplit,
   handleReduce,
   handleDelete,
}) => {
   const { theme } = useContext(ThemeContext);
   const isDark = theme === "dark";

   const { setAlert } = useContext(AlertContext);
   const { renameTransaction, reduceTransactionAmount, updateCategory, splitTransaction, deleteTransaction } = useContext(transactionContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const { categories } = useContext(categoryContext);

   // Helper to reliably retrieve all categories for a category type
   const getCategoriesForType = (ct) => {
      const globalCats = (categories || []).filter(
         (c) => c.categoryType && (c.categoryType.categoryTypeId === ct.categoryTypeId || c.categoryType.name === ct.name)
      );
      const typeCats = ct.categories || [];
      const map = new Map();
      [...typeCats, ...globalCats].forEach((c) => {
         if (c && c.categoryId) map.set(c.categoryId, c);
      });
      return Array.from(map.values());
   };

   // Active View State: 'MENU' | 'RENAME' | 'ASSIGN' | 'SPLIT' | 'REDUCE' | 'DELETE'
   const [activeView, setActiveView] = useState("MENU");

   // Form states
   const [editedName, setEditedName] = useState("");
   const [reducedAmount, setReducedAmount] = useState("");
   const [splitTransactions, setSplitTransactions] = useState([
      { name: "", amount: "", removable: false },
      { name: "", amount: "", removable: false },
   ]);

   // Reset state on open / transaction change
   useEffect(() => {
      if (transaction) {
         setActiveView("MENU");
         setEditedName(transaction.name || "");
         setReducedAmount("");
         setSplitTransactions([
            { name: `${transaction.name} (Part 1)`, amount: "", removable: false },
            { name: `${transaction.name} (Part 2)`, amount: "", removable: false },
         ]);
      }
   }, [transaction, isOpen]);

   if (!transaction || !isOpen) return null;

   // Handler: Rename Confirm
   const confirmRename = () => {
      if (!editedName.trim()) {
         setAlert("Please enter a valid transaction name", "danger");
         return;
      }
      renameTransaction(transaction.transactionId, editedName.trim());
      setAlert("Transaction renamed successfully", "success");
      onClose();
   };

   // Handler: Assign Category
   const confirmAssignCategory = (cat) => {
      updateCategory(transaction.transactionId, cat.categoryId);
      setAlert(`Transaction assigned to ${cat.name}`, "success");
      onClose();
   };

   // Handler: Reduce Confirm
   const confirmReduce = () => {
      const amt = parseFloat(reducedAmount);
      if (isNaN(amt) || amt <= 0) {
         setAlert("Please enter a valid positive amount.", "danger");
         return;
      }
      if (amt >= parseFloat(transaction.amount)) {
         setAlert("The reduced amount must be less than the original transaction amount.", "danger");
         return;
      }
      reduceTransactionAmount(transaction.transactionId, amt);
      setAlert(`Amount reduced to $${amt.toFixed(2)}`, "success");
      onClose();
   };

   // Handler: Split Confirm
   const confirmSplit = () => {
      const filled = splitTransactions.filter((s) => s.name && s.amount);
      if (filled.length < 2) {
         setAlert("Please fill out at least two split portions.", "danger");
         return;
      }
      const sum = filled.reduce((acc, curr) => acc + parseFloat(curr.amount || 0), 0);
      if (sum > parseFloat(transaction.amount)) {
         setAlert("Total split amount cannot exceed the original transaction amount.", "danger");
         return;
      }
      const dtos = filled.map((s) => ({
         updatedName: s.name,
         updatedAmount: parseFloat(s.amount),
      }));
      splitTransaction(transaction.transactionId, { splitTransactions: dtos });
      setAlert("Transaction split successfully", "success");
      onClose();
   };

   // Handler: Delete Confirm
   const confirmDelete = () => {
      deleteTransaction(transaction.transactionId);
      setAlert("Transaction deleted", "success");
      onClose();
   };

   // Modal Title based on View
   const getTitle = () => {
      switch (activeView) {
         case "RENAME": return "Rename Transaction";
         case "ASSIGN": return "Assign Category";
         case "SPLIT": return "Split Transaction";
         case "REDUCE": return "Reduce Amount";
         case "DELETE": return "Delete Transaction";
         default: return "Transaction Actions";
      }
   };

   const menuActions = [
      {
         id: "RENAME",
         label: "Rename Transaction",
         desc: "Update merchant or transaction display name",
         icon: FaPen,
         color: isDark ? "text-indigo-400 bg-indigo-500/10 border-indigo-500/20 hover:bg-indigo-500/20" : "text-indigo-600 bg-indigo-50 border-indigo-200 hover:bg-indigo-100",
      },
      {
         id: "ASSIGN",
         label: "Assign / Move Category",
         desc: "Change assigned budget category",
         icon: FaTags,
         color: isDark ? "text-emerald-400 bg-emerald-500/10 border-emerald-500/20 hover:bg-emerald-500/20" : "text-emerald-600 bg-emerald-50 border-emerald-200 hover:bg-emerald-100",
      },
      {
         id: "SPLIT",
         label: "Split Transaction",
         desc: "Divide total amount into multiple portions",
         icon: FaCut,
         color: isDark ? "text-purple-400 bg-purple-500/10 border-purple-500/20 hover:bg-purple-500/20" : "text-purple-600 bg-purple-50 border-purple-200 hover:bg-purple-100",
      },
      {
         id: "REDUCE",
         label: "Reduce Amount",
         desc: "Lower dollar amount of transaction",
         icon: FaCompressAlt,
         color: isDark ? "text-amber-400 bg-amber-500/10 border-amber-500/20 hover:bg-amber-500/20" : "text-amber-700 bg-amber-50 border-amber-200 hover:bg-amber-100",
      },
      {
         id: "DELETE",
         label: "Delete Transaction",
         desc: "Permanently remove from budget",
         icon: FaTrashAlt,
         color: isDark ? "text-red-400 bg-red-500/10 border-red-500/20 hover:bg-red-500/20" : "text-red-600 bg-red-50 border-red-200 hover:bg-red-100",
      },
   ];

   return (
      <Modal isOpen={isOpen} onClose={onClose} title={getTitle()} size={activeView === "ASSIGN" ? "lg" : "md"}>
         <div className="flex flex-col gap-4">
            {/* Back to Options navigation bar when inside sub-action */}
            {activeView !== "MENU" && (
               <button
                  type="button"
                  onClick={() => setActiveView("MENU")}
                  className={`self-start flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold transition-all border ${
                     isDark
                        ? "text-slate-300 bg-slate-800 border-slate-700 hover:bg-slate-700 hover:text-white"
                        : "text-slate-700 bg-slate-100 border-slate-200 hover:bg-slate-200"
                  }`}
               >
                  <FaArrowLeft size={10} />
                  <span>Back to Options</span>
               </button>
            )}

            {/* Transaction Header Info Card */}
            <div className={`p-3.5 rounded-xl border flex items-center justify-between ${
               isDark ? "bg-slate-800/80 border-slate-700/60" : "bg-slate-50 border-slate-200"
            }`}>
               <div className="flex items-center gap-3">
                  <img
                     src={
                        transaction.logoUrl ||
                        "https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                     }
                     alt="Logo"
                     className="w-9 h-9 rounded-full object-cover border border-slate-400/30"
                  />
                  <div>
                     <h4 className={`text-sm font-bold mb-0.5 ${isDark ? "text-white" : "text-slate-900"}`}>
                        {transaction.name}
                     </h4>
                     <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        {new Date(transaction.date).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}
                     </p>
                  </div>
               </div>
               <span className={`text-base font-black ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                  ${Math.round(transaction.amount)}
               </span>
            </div>

            {/* VIEW 0: MAIN MENU */}
            {activeView === "MENU" && (
               <div className="flex flex-col gap-2.5 mt-1">
                  {menuActions.map((act) => {
                     const Icon = act.icon;
                     return (
                        <button
                           key={act.id}
                           onClick={() => {
                              if (handleRename && act.id === "RENAME") {
                                 // Allow direct view switch
                                 setActiveView("RENAME");
                              } else {
                                 setActiveView(act.id);
                              }
                           }}
                           className={`w-full flex items-center gap-3.5 p-3 rounded-xl border text-left transition-all duration-200 cursor-pointer ${act.color}`}
                        >
                           <div className="p-2 rounded-lg bg-white/20">
                              <Icon size={16} />
                           </div>
                           <div>
                              <p className="text-sm font-bold m-0 leading-tight">
                                 {act.label}
                              </p>
                              <p className="text-xs opacity-75 m-0 leading-tight mt-0.5">
                                 {act.desc}
                              </p>
                           </div>
                        </button>
                     );
                  })}
               </div>
            )}

            {/* VIEW 1: RENAME */}
            {activeView === "RENAME" && (
               <div className="flex flex-col gap-4">
                  <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                     Enter the new display or merchant name for this transaction.
                  </p>
                  <input
                     type="text"
                     value={editedName}
                     onChange={(e) => setEditedName(e.target.value)}
                     placeholder="New Transaction Name"
                     className={`w-full px-3.5 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                        isDark
                           ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                           : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400 focus:bg-white"
                     }`}
                  />
                  <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
                     <button
                        type="button"
                        onClick={() => setActiveView("MENU")}
                        className={`px-4 py-2 text-sm font-semibold rounded-lg ${
                           isDark ? "bg-slate-800 text-slate-300 hover:bg-slate-700" : "bg-slate-100 text-slate-700 hover:bg-slate-200"
                        }`}
                     >
                        Cancel
                     </button>
                     <button
                        type="button"
                        onClick={confirmRename}
                        className="px-5 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg shadow-md"
                     >
                        Save Name
                     </button>
                  </div>
               </div>
            )}

            {/* VIEW 2: ASSIGN CATEGORY */}
            {activeView === "ASSIGN" && (
               <div className="flex flex-col gap-3 max-h-[50vh] overflow-y-auto pr-1">
                  <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                     Select a category to assign this transaction to:
                  </p>
                  {categoryTypes?.map((ct) => {
                     const catList = getCategoriesForType(ct);
                     return (
                        <div
                           key={ct.categoryTypeId}
                           className={`p-3.5 border rounded-xl flex flex-col gap-2.5 ${
                              isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-2 border-slate-300"
                           }`}
                        >
                           <h3 className="text-xs font-bold text-indigo-500 uppercase tracking-wider">
                              {ct.name}
                           </h3>
                           <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                              {catList?.map((cat) => (
                                 <button
                                    key={cat.categoryId}
                                    type="button"
                                    onClick={() => confirmAssignCategory(cat)}
                                    className={`px-3 py-2 rounded-lg text-xs font-bold transition-all border text-left truncate ${
                                       isDark
                                          ? "bg-slate-900 border-slate-700 text-slate-200 hover:bg-indigo-600 hover:border-indigo-500 hover:text-white"
                                          : "bg-white border-2 border-slate-300 text-slate-900 hover:bg-indigo-600 hover:border-indigo-600 hover:text-white shadow-sm"
                                    }`}
                                 >
                                    {cat.name}
                                 </button>
                              ))}
                           </div>
                        </div>
                     );
                  })}
               </div>
            )}

            {/* VIEW 3: REDUCE AMOUNT */}
            {activeView === "REDUCE" && (
               <div className="flex flex-col gap-4">
                  <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                     Enter a new reduced amount for this transaction (original: ${transaction.amount}).
                  </p>
                  <div className="relative flex items-center w-full">
                     <span className={`absolute left-3.5 text-sm font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>$</span>
                     <input
                        type="number"
                        step="0.01"
                        value={reducedAmount}
                        onChange={(e) => setReducedAmount(e.target.value)}
                        placeholder="New Amount"
                        className={`w-full pl-8 pr-3.5 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                           isDark
                              ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                              : "bg-slate-50 border border-slate-300 text-slate-900 placeholder-slate-400 focus:bg-white"
                        }`}
                     />
                  </div>

                  {/* Percentage Chips */}
                  <div className="flex gap-2 justify-center">
                     {[10, 25, 50, 75].map((pct) => (
                        <button
                           key={pct}
                           type="button"
                           onClick={() => {
                              const calc = (transaction.amount * (1 - pct / 100)).toFixed(2);
                              setReducedAmount(calc);
                           }}
                           className={`px-3 py-1 text-xs font-semibold rounded-lg border transition-all ${
                              isDark
                                 ? "bg-slate-800 border-slate-700 text-slate-300 hover:bg-indigo-600 hover:text-white"
                                 : "bg-slate-100 border-slate-200 text-slate-700 hover:bg-indigo-600 hover:text-white"
                           }`}
                        >
                           -{pct}%
                        </button>
                     ))}
                  </div>

                  <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
                     <button
                        type="button"
                        onClick={() => setActiveView("MENU")}
                        className={`px-4 py-2 text-sm font-semibold rounded-lg ${
                           isDark ? "bg-slate-800 text-slate-300 hover:bg-slate-700" : "bg-slate-100 text-slate-700 hover:bg-slate-200"
                        }`}
                     >
                        Cancel
                     </button>
                     <button
                        type="button"
                        onClick={confirmReduce}
                        className="px-5 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg shadow-md"
                     >
                        Reduce Amount
                     </button>
                  </div>
               </div>
            )}

            {/* VIEW 4: SPLIT TRANSACTION */}
            {activeView === "SPLIT" && (
               <div className="flex flex-col gap-4">
                  <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                     Divide the transaction amount (${transaction.amount}) into separate portions.
                  </p>
                  <div className="flex flex-col gap-3 max-h-[45vh] overflow-y-auto pr-1">
                     {splitTransactions.map((split, idx) => (
                        <div
                           key={idx}
                           className={`relative p-3 border rounded-xl flex flex-col gap-2 ${
                              isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-slate-200"
                           }`}
                        >
                           {splitTransactions.length > 2 && (
                              <button
                                 type="button"
                                 className="absolute top-2 right-2 p-1 text-slate-400 hover:text-red-400"
                                 onClick={() => {
                                    const list = [...splitTransactions];
                                    list.splice(idx, 1);
                                    setSplitTransactions(list);
                                 }}
                              >
                                 <FaTimes size={12} />
                              </button>
                           )}
                           <input
                              type="text"
                              value={split.name}
                              onChange={(e) => {
                                 const list = [...splitTransactions];
                                 list[idx].name = e.target.value;
                                 setSplitTransactions(list);
                              }}
                              placeholder="Portion Name"
                              className={`w-full px-3 py-1.5 rounded-lg text-xs focus:outline-none ${
                                 isDark ? "bg-slate-900 border border-slate-700 text-slate-100" : "bg-white border border-slate-300 text-slate-900"
                              }`}
                           />
                           <div className="relative flex items-center">
                              <span className={`absolute left-3 text-xs ${isDark ? "text-slate-400" : "text-slate-500"}`}>$</span>
                              <input
                                 type="number"
                                 step="0.01"
                                 value={split.amount}
                                 onChange={(e) => {
                                    const list = [...splitTransactions];
                                    list[idx].amount = e.target.value;
                                    setSplitTransactions(list);
                                 }}
                                 placeholder="Amount"
                                 className={`w-full pl-6 pr-3 py-1.5 rounded-lg text-xs focus:outline-none ${
                                    isDark ? "bg-slate-900 border border-slate-700 text-slate-100" : "bg-white border border-slate-300 text-slate-900"
                                 }`}
                              />
                           </div>
                        </div>
                     ))}
                  </div>

                  <button
                     type="button"
                     onClick={() => {
                        setSplitTransactions([...splitTransactions, { name: "", amount: "", removable: true }]);
                     }}
                     className="self-center flex items-center justify-center w-8 h-8 bg-brand-600 hover:bg-brand-500 text-white rounded-full transition-colors shadow-md"
                     title="Add another split portion"
                  >
                     <FaPlus size={12} />
                  </button>

                  <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
                     <button
                        type="button"
                        onClick={() => setActiveView("MENU")}
                        className={`px-4 py-2 text-sm font-semibold rounded-lg ${
                           isDark ? "bg-slate-800 text-slate-300 hover:bg-slate-700" : "bg-slate-100 text-slate-700 hover:bg-slate-200"
                        }`}
                     >
                        Cancel
                     </button>
                     <button
                        type="button"
                        onClick={confirmSplit}
                        className="px-5 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg shadow-md"
                     >
                        Confirm Split
                     </button>
                  </div>
               </div>
            )}

            {/* VIEW 5: DELETE */}
            {activeView === "DELETE" && (
               <div className="flex flex-col gap-4">
                  <div className={`p-4 rounded-xl border flex items-center gap-3 ${
                     isDark ? "bg-red-500/10 border-red-500/20 text-red-300" : "bg-red-50 border-red-200 text-red-900"
                  }`}>
                     <FaTrashAlt size={20} className="flex-shrink-0 text-red-500" />
                     <p className="text-xs font-semibold m-0 leading-relaxed">
                        Are you sure you want to delete transaction <strong>"{transaction.name}"</strong> (${transaction.amount})? This action cannot be undone.
                     </p>
                  </div>

                  <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
                     <button
                        type="button"
                        onClick={() => setActiveView("MENU")}
                        className={`px-4 py-2 text-sm font-semibold rounded-lg ${
                           isDark ? "bg-slate-800 text-slate-300 hover:bg-slate-700" : "bg-slate-100 text-slate-700 hover:bg-slate-200"
                        }`}
                     >
                        Cancel
                     </button>
                     <button
                        type="button"
                        onClick={confirmDelete}
                        className="px-5 py-2 text-sm font-bold text-white bg-red-600 hover:bg-red-500 rounded-lg shadow-md"
                     >
                        Delete Transaction
                     </button>
                  </div>
               </div>
            )}
         </div>
      </Modal>
   );
};

export default TransactionActionModal;
