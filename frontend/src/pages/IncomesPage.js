import React, { useContext, useEffect, useRef, useState, useMemo } from "react";
import { FaPlus, FaTrash, FaWallet, FaEdit } from "react-icons/fa";
import IncomeContext from "../context/income/incomeContext";
import CategoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryContext from "../context/category/categoryContext";
import alertContext from "../context/alert/alertContext";
import ConfirmationModal from "../components/layout/ConfirmationModal";
import Modal from "../components/layout/Modal";
import { ThemeContext } from "../context/theme/ThemeContext";

/**
 * Dedicated Incomes Page – "Your Incomes" layout mirroring AccountsPage structure.
 */
const IncomesPage = () => {
   const { incomes, fetchIncomes, addIncome, updateIncome, removeIncome, setLoading } = useContext(IncomeContext);
   const { fetchCategoryTypes } = useContext(CategoryTypeContext);
   const { fetchCategories } = useContext(CategoryContext);
   const { setAlert } = useContext(alertContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const initialFetchRef = useRef(false);

   const [showAddModal, setShowAddModal] = useState(false);
   const [showEditModal, setShowEditModal] = useState(false);
   const [showConfirmModal, setShowConfirmModal] = useState(false);
   const [incomeToDelete, setIncomeToDelete] = useState(null);
   const [incomeToEdit, setIncomeToEdit] = useState(null);

   // Form states
   const [amount, setAmount] = useState("");
   const [description, setDescription] = useState("");
   const [incomeSource, setIncomeSource] = useState("EMPLOYER");
   const [incomeType, setIncomeType] = useState("SALARY");

   useEffect(() => {
      if (!initialFetchRef.current || !incomes) {
         setLoading();
         fetchIncomes();
         initialFetchRef.current = true;
      }
   }, [incomes, fetchIncomes, setLoading]);

   const totalMonthlyIncome = useMemo(() => {
      if (!incomes) return 0;
      return incomes.reduce((sum, inc) => sum + (parseFloat(inc.amount) || 0), 0);
   }, [incomes]);

   const handleOpenEdit = (inc) => {
      setIncomeToEdit(inc);
      setAmount(inc.amount ? inc.amount.toString() : "");
      setShowEditModal(true);
   };

   const handleSubmitAdd = async (e) => {
      e.preventDefault();
      if (!amount || isNaN(parseFloat(amount)) || parseFloat(amount) <= 0) {
         setAlert("Please enter a valid positive income amount", "danger");
         return;
      }
      await addIncome({ amount: parseFloat(amount), incomeSource, incomeType, description });
      if (fetchCategoryTypes) fetchCategoryTypes();
      if (fetchCategories) fetchCategories();
      setAlert("Income source added successfully", "success");
      setAmount("");
      setDescription("");
      setIncomeSource("EMPLOYER");
      setIncomeType("SALARY");
      setShowAddModal(false);
   };

   const handleSaveEdit = async (e) => {
      e.preventDefault();
      if (!amount || isNaN(parseFloat(amount)) || parseFloat(amount) <= 0) {
         setAlert("Please enter a valid income amount", "danger");
         return;
      }
      await updateIncome({ incomeId: incomeToEdit.incomeId, amount: parseFloat(amount) });
      if (fetchCategoryTypes) fetchCategoryTypes();
      if (fetchCategories) fetchCategories();
      setAlert("Income updated successfully", "success");
      setShowEditModal(false);
      setIncomeToEdit(null);
   };

   const handleConfirmDelete = async () => {
      if (incomeToDelete) {
         try {
            const id = incomeToDelete.incomeId || incomeToDelete.id;
            await removeIncome(id);
            await fetchIncomes();
            if (fetchCategoryTypes) fetchCategoryTypes();
            if (fetchCategories) fetchCategories();
            setAlert("Income source removed", "success");
         } catch (err) {
            console.error(err);
            setAlert("Failed to remove income source", "danger");
         } finally {
            setShowConfirmModal(false);
            setIncomeToDelete(null);
         }
      }
   };

   return (
      <div className={`flex flex-col min-h-screen relative ${
         isDark
            ? "bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 text-slate-100"
            : "bg-gradient-to-br from-slate-100 via-indigo-50/50 to-slate-100 text-slate-800"
      }`}>
         <div className="flex flex-col items-center px-4 md:px-12 h-full pt-16">

            {/* Header */}
            <div className="max-w-xl w-full text-center mb-6 mt-5">
               <h2 className={`text-4xl md:text-5xl font-black mb-2 ${isDark ? "text-white" : "text-slate-900"}`}>
                  Your Incomes
               </h2>
               <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                  View and manage all active income streams
               </p>
            </div>

            {/* Total Monthly Income Banner */}
            {incomes && incomes.length > 0 && (
               <div className={`w-full max-w-xl border rounded-2xl p-5 mb-6 shadow-lg ${
                  isDark
                     ? "bg-slate-800/70 border-slate-600/50"
                     : "bg-white border-slate-200"
               }`}>
                  <div className="text-center">
                     <p className={`text-xs font-bold uppercase tracking-wider mb-1 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        Total Monthly Income
                     </p>
                     <p className={`text-3xl font-black ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                        ${totalMonthlyIncome.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                     </p>
                     <p className={`text-xs mt-1 ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                        Across {incomes.length} income source{incomes.length !== 1 ? "s" : ""}
                     </p>
                  </div>
               </div>
            )}

            {/* Income List */}
            <div className="w-full max-w-xl flex flex-col gap-2.5 pb-6">
               {incomes && incomes.length > 0 ? (
                  incomes.map((inc, index) => (
                     <div
                        key={inc.incomeId || index}
                        className={`flex items-center justify-between p-4 border rounded-2xl transition-all ${
                           isDark
                              ? "bg-slate-800/70 border-slate-700/70 hover:border-slate-600"
                              : "bg-white border-slate-200 shadow-sm hover:border-slate-300"
                        }`}
                     >
                        <div className="flex items-center gap-3">
                           <div className={`p-2.5 rounded-xl border ${
                              isDark
                                 ? "bg-brand-500/10 border-brand-500/20 text-brand-400"
                                 : "bg-brand-50 border-brand-100 text-brand-600"
                           }`}>
                              <FaWallet className="w-4 h-4" />
                           </div>
                           <div className="flex flex-col">
                              <span className={`text-sm font-bold ${isDark ? "text-slate-100" : "text-slate-900"}`}>
                                 {inc.description || inc.incomeSource || `Income Source ${index + 1}`}
                              </span>
                              <span className={`text-[10px] font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-brand-600"}`}>
                                 {inc.incomeType || "Income"}{inc.incomeSource ? ` · ${inc.incomeSource}` : ""}
                              </span>
                           </div>
                        </div>

                        <div className="flex items-center gap-3">
                           <span className={`text-base font-extrabold ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                              +${parseFloat(inc.amount || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                           </span>
                           <div className="flex items-center gap-1">
                              <button
                                 type="button"
                                 onClick={() => handleOpenEdit(inc)}
                                 className={`p-1.5 rounded-lg transition-colors ${
                                    isDark
                                       ? "text-slate-400 hover:text-white hover:bg-slate-700"
                                       : "text-slate-500 hover:text-slate-900 hover:bg-slate-100"
                                 }`}
                                 title="Edit Income"
                              >
                                 <FaEdit className="w-3.5 h-3.5" />
                              </button>
                              <button
                                 type="button"
                                 onClick={() => { setIncomeToDelete(inc); setShowConfirmModal(true); }}
                                 className={`p-1.5 rounded-lg transition-colors ${
                                    isDark
                                       ? "text-slate-400 hover:text-red-400 hover:bg-slate-700"
                                       : "text-slate-500 hover:text-red-500 hover:bg-slate-100"
                                 }`}
                                 title="Delete Income"
                              >
                                 <FaTrash className="w-3.5 h-3.5" />
                              </button>
                           </div>
                        </div>
                     </div>
                  ))
               ) : (
                  <div className={`p-8 text-center border rounded-2xl ${
                     isDark ? "bg-slate-800/60 border-slate-700 text-slate-500" : "bg-white border-slate-200 text-slate-400 shadow-sm"
                  }`}>
                     No income sources added yet. Add your first income source below!
                  </div>
               )}
            </div>

            {/* Add Income Button */}
            <div className="flex justify-center mb-20 w-full max-w-xl">
               <button
                  type="button"
                  onClick={() => setShowAddModal(true)}
                  className="flex items-center gap-2 bg-brand-600 hover:bg-brand-500 text-white rounded-xl px-6 py-3 text-sm font-bold transition-all duration-300 shadow-lg hover:scale-105"
               >
                  <FaPlus size={12} />
                  Add Income Source
               </button>
            </div>
         </div>

         {/* Add Income Modal */}
         {showAddModal && (
            <Modal isOpen={true} onClose={() => setShowAddModal(false)} title="Add Income Source" size="md">
               <form onSubmit={handleSubmitAdd} className="flex flex-col gap-4">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                     <div className="flex flex-col gap-1">
                        <label className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>Amount ($)</label>
                        <input
                           type="number"
                           step="0.01"
                           value={amount}
                           onChange={(e) => setAmount(e.target.value)}
                           placeholder="0.00"
                           className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/50 ${
                              isDark
                                 ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                                 : "bg-white border border-slate-300 text-slate-900 placeholder-slate-400"
                           }`}
                           required
                        />
                     </div>
                     <div className="flex flex-col gap-1">
                        <label className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>Description</label>
                        <input
                           type="text"
                           value={description}
                           onChange={(e) => setDescription(e.target.value)}
                           placeholder="e.g. Primary Job"
                           className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/50 ${
                              isDark
                                 ? "bg-slate-900 border border-slate-700 text-slate-100 placeholder-slate-500"
                                 : "bg-white border border-slate-300 text-slate-900 placeholder-slate-400"
                           }`}
                        />
                     </div>
                     <div className="flex flex-col gap-1">
                        <label className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>Income Source</label>
                        <select
                           value={incomeSource}
                           onChange={(e) => setIncomeSource(e.target.value)}
                           className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/50 ${
                              isDark
                                 ? "bg-slate-900 border border-slate-700 text-slate-100"
                                 : "bg-white border border-slate-300 text-slate-900"
                           }`}
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
                        <label className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>Income Type</label>
                        <select
                           value={incomeType}
                           onChange={(e) => setIncomeType(e.target.value)}
                           className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/50 ${
                              isDark
                                 ? "bg-slate-900 border border-slate-700 text-slate-100"
                                 : "bg-white border border-slate-300 text-slate-900"
                           }`}
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
                  <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
                     <button
                        type="button"
                        onClick={() => setShowAddModal(false)}
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
                        className="px-5 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg shadow-md"
                     >
                        Save Income
                     </button>
                  </div>
               </form>
            </Modal>
         )}

         {/* Edit Income Modal */}
         {showEditModal && (
            <Modal isOpen={true} onClose={() => setShowEditModal(false)} title="Edit Income Amount" size="md">
               <form onSubmit={handleSaveEdit} className="flex flex-col gap-4">
                  <div className="flex flex-col gap-1">
                     <label className={`text-xs font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>Monthly Amount ($)</label>
                     <input
                        type="number"
                        step="0.01"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        className={`px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/50 ${
                           isDark
                              ? "bg-slate-900 border border-slate-700 text-slate-100"
                              : "bg-white border border-slate-300 text-slate-900"
                        }`}
                        required
                     />
                  </div>
                  <div className={`flex justify-end gap-3 pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
                     <button
                        type="button"
                        onClick={() => setShowEditModal(false)}
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
                        className="px-4 py-2 text-sm font-bold text-white bg-brand-600 hover:bg-brand-500 rounded-lg shadow-md"
                     >
                        Save Changes
                     </button>
                  </div>
               </form>
            </Modal>
         )}

         {/* Delete Confirmation Modal */}
         {showConfirmModal && incomeToDelete && (
            <ConfirmationModal
               question={`Are you sure you want to remove this income source (${incomeToDelete.description || incomeToDelete.incomeSource})?`}
               onConfirm={handleConfirmDelete}
               onClose={() => setShowConfirmModal(false)}
            />
         )}
      </div>
   );
};

export default IncomesPage;
