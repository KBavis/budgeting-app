import React, { useContext, useState } from "react";
import Modal from "../layout/Modal";
import IncomeContext from "../../context/income/incomeContext";
import AlertContext from "../../context/alert/alertContext";
import { FaPlus, FaTrash, FaWallet } from "react-icons/fa";

/**
 * IncomeManagementModal — Manage, add, and view income sources
 */
const IncomeManagementModal = ({ isOpen, onClose }) => {
   const { incomes, addIncome, removeIncome } = useContext(IncomeContext);
   const { setAlert } = useContext(AlertContext);

   const [showAddForm, setShowAddForm] = useState(false);
   const [amount, setAmount] = useState("");
   const [description, setDescription] = useState("");
   const [incomeSource, setIncomeSource] = useState("EMPLOYER");
   const [incomeType, setIncomeType] = useState("SALARY");

   const totalMonthlyIncome = (incomes || []).reduce(
      (sum, inc) => sum + (parseFloat(inc.amount) || 0),
      0
   );

   const handleSubmitAdd = (e) => {
      e.preventDefault();
      if (!amount || isNaN(parseFloat(amount)) || parseFloat(amount) <= 0) {
         setAlert("Please enter a valid positive income amount", "danger");
         return;
      }

      addIncome({
         amount: parseFloat(amount),
         incomeSource,
         incomeType,
         description,
      });

      setAlert("Income source added successfully", "success");
      setAmount("");
      setDescription("");
      setShowAddForm(false);
   };

   const handleDelete = (incomeId) => {
      removeIncome(incomeId);
      setAlert("Income source removed", "success");
   };

   return (
      <Modal isOpen={isOpen} onClose={onClose} title="Manage Monthly Income" size="lg">
         <div className="flex flex-col gap-6">
            {/* Total Summary Header */}
            <div className="p-4 bg-slate-800/80 border border-slate-700/80 rounded-xl flex items-center justify-between shadow-inner">
               <div className="flex items-center gap-3">
                  <div className="p-3 bg-brand-500/20 text-brand-400 rounded-xl border border-brand-500/30">
                     <FaWallet className="w-6 h-6" />
                  </div>
                  <div>
                     <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                        Total Monthly Income
                     </p>
                     <p className="text-2xl font-bold text-white">
                        ${totalMonthlyIncome.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                     </p>
                  </div>
               </div>

               <button
                  type="button"
                  onClick={() => setShowAddForm(!showAddForm)}
                  className="flex items-center gap-1.5 px-3.5 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-xl text-xs font-bold transition-all shadow-md"
               >
                  <FaPlus className="w-3 h-3" />
                  <span>{showAddForm ? "Cancel" : "Add Income"}</span>
               </button>
            </div>

            {/* Add New Income Form */}
            {showAddForm && (
               <form
                  onSubmit={handleSubmitAdd}
                  className="p-4 bg-slate-800/50 border border-slate-700/60 rounded-xl flex flex-col gap-3 animate-fade-in"
               >
                  <h4 className="text-sm font-bold text-slate-200 mb-1">
                     Add New Income Source
                  </h4>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                     <div className="flex flex-col gap-1">
                        <label className="text-[11px] font-semibold text-slate-400">
                           Amount ($)
                        </label>
                        <input
                           type="number"
                           step="0.01"
                           value={amount}
                           onChange={(e) => setAmount(e.target.value)}
                           placeholder="0.00"
                           className="bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-xs focus:outline-none focus:border-brand-500"
                           required
                        />
                     </div>

                     <div className="flex flex-col gap-1">
                        <label className="text-[11px] font-semibold text-slate-400">
                           Description
                        </label>
                        <input
                           type="text"
                           value={description}
                           onChange={(e) => setDescription(e.target.value)}
                           placeholder="e.g. Primary Job"
                           className="bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-xs focus:outline-none focus:border-brand-500"
                        />
                     </div>

                     <div className="flex flex-col gap-1">
                        <label className="text-[11px] font-semibold text-slate-400">
                           Income Source
                        </label>
                        <select
                           value={incomeSource}
                           onChange={(e) => setIncomeSource(e.target.value)}
                           className="bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-xs focus:outline-none focus:border-brand-500"
                        >
                           <option value="EMPLOYER">Employer</option>
                           <option value="CLIENT">Client</option>
                           <option value="PROPERTY">Property</option>
                           <option value="STOCK">Stock / Dividends</option>
                           <option value="BUSINESS">Business</option>
                           <option value="OTHER">Other</option>
                        </select>
                     </div>

                     <div className="flex flex-col gap-1">
                        <label className="text-[11px] font-semibold text-slate-400">
                           Income Type
                        </label>
                        <select
                           value={incomeType}
                           onChange={(e) => setIncomeType(e.target.value)}
                           className="bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-xs focus:outline-none focus:border-brand-500"
                        >
                           <option value="SALARY">Salary</option>
                           <option value="BONUS">Bonus</option>
                           <option value="FREELANCE">Freelance</option>
                           <option value="RENTAL">Rental</option>
                           <option value="INVESTMENT">Investment</option>
                           <option value="OTHER">Other</option>
                        </select>
                     </div>
                  </div>

                  <button
                     type="submit"
                     className="mt-2 self-end px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-bold transition-all shadow-md"
                  >
                     Save Income
                  </button>
               </form>
            )}

            {/* Income Sources List */}
            <div className="flex flex-col gap-2 max-h-60 overflow-y-auto">
               <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1 text-left">
                  Income Sources ({incomes ? incomes.length : 0})
               </p>
               {incomes && incomes.length > 0 ? (
                  incomes.map((inc, index) => (
                     <div
                        key={inc.incomeId || index}
                        className="relative p-3.5 bg-slate-800/50 border border-slate-700/60 rounded-xl"
                     >
                        <button
                           type="button"
                           onClick={() => handleDelete(inc.incomeId)}
                           className="absolute top-3 right-3 p-1.5 text-slate-400 hover:text-red-400 rounded-lg hover:bg-slate-700 transition-colors z-10"
                           title="Delete Income"
                        >
                           <FaTrash className="w-3.5 h-3.5" />
                        </button>

                        <div className="flex items-center justify-between pr-8">
                           <div className="flex flex-col text-left min-w-0 flex-1 pr-2">
                              <span className="text-sm font-bold text-slate-100 truncate">
                                 {inc.description || inc.incomeSource || `Income Source ${index + 1}`}
                              </span>
                              <span className="text-[10px] text-slate-400 font-semibold uppercase tracking-wider mt-0.5">
                                 {inc.incomeType || "Income"}{inc.incomeSource ? ` · ${inc.incomeSource}` : ""}
                              </span>
                           </div>
                           <div className="flex-shrink-0">
                              <span className="text-base font-bold text-emerald-400">
                                 +${parseFloat(inc.amount || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                              </span>
                           </div>
                        </div>
                     </div>
                  ))
               ) : (
                  <p className="text-xs text-slate-500 italic py-4 text-center">
                     No income sources added yet. Click "Add Income" above to add one.
                  </p>
               )}
            </div>

            {/* Close Button */}
            <div className="flex justify-end pt-4 border-t border-slate-800">
               <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white rounded-xl text-xs font-semibold transition-colors"
               >
                  Done
               </button>
            </div>
         </div>
      </Modal>
   );
};

export default IncomeManagementModal;
