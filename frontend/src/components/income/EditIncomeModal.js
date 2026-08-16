import React, { useContext, useState } from "react";
import IncomeContext from "../../context/income/incomeContext";
import CategoryTypeContext from "../../context/category/types/categoryTypeContext";
import CategoryContext from "../../context/category/categoryContext";
import AlertContext from "../../context/alert/alertContext";
import Modal from "../layout/Modal";
import { FaPlus, FaPencilAlt } from "react-icons/fa";

const EditIncomeModal = ({ onClose }) => {
  const { incomes, addIncome, updateIncome } = useContext(IncomeContext);
  const { fetchCategoryTypes } = useContext(CategoryTypeContext);
  const { fetchCategories } = useContext(CategoryContext);
  const { setAlert } = useContext(AlertContext);

  const [editingId, setEditingId] = useState(null);
  const [editAmount, setEditAmount] = useState("");
  const [editDescription, setEditDescription] = useState("");

  const [newAmount, setNewAmount] = useState("");
  const [newSource, setNewSource] = useState("EMPLOYER");
  const [newType, setNewType] = useState("SALARY");
  const [newDescription, setNewDescription] = useState("");
  const [isAdding, setIsAdding] = useState(false);

  const incomeSources = [
    "EMPLOYER", "CLIENT", "PROPERTY", "STOCK", "SAVINGS_ACCOUNT",
    "RETIREMENT_ACCOUNT", "GOVERNMENT", "BUSINESS", "OTHER"
  ];

  const incomeTypes = [
    "SALARY", "BONUS", "COMMISSION", "FREELANCE", "RENTAL",
    "INVESTMENT", "PENSION", "GIFT", "OTHER"
  ];

  const handleStartEdit = (income) => {
    setEditingId(income.incomeId);
    setEditAmount(income.amount);
    setEditDescription(income.description || "");
  };

  const handleSaveEdit = async (income) => {
    const parsedAmount = parseFloat(editAmount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      setAlert("Please enter a valid positive income amount.", "danger");
      return;
    }
    await updateIncome({
      incomeId: income.incomeId,
      amount: parsedAmount,
      description: editDescription,
      incomeType: income.incomeType,
      incomeSource: income.incomeSource,
    });
    if (fetchCategoryTypes) fetchCategoryTypes();
    if (fetchCategories) fetchCategories();
    setEditingId(null);
    setAlert("Income updated successfully!", "success");
  };

  const handleAddSubmit = async (e) => {
    e.preventDefault();
    const parsedAmount = parseFloat(newAmount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      setAlert("Please enter a valid positive income amount.", "danger");
      return;
    }
    await addIncome({
      amount: parsedAmount,
      incomeSource: newSource,
      incomeType: newType,
      description: newDescription,
    });
    if (fetchCategoryTypes) fetchCategoryTypes();
    if (fetchCategories) fetchCategories();
    setNewAmount("");
    setNewDescription("");
    setIsAdding(false);
    setAlert("New income source added!", "success");
  };

  const totalMonthlyIncome = (incomes || []).reduce((acc, inc) => acc + (inc.amount || 0), 0);

  return (
    <Modal isOpen={true} onClose={onClose} title="Manage Monthly Income" size="lg">
      <div className="flex flex-col gap-5">
        <div className="p-4 bg-brand-900/40 border border-brand-500/30 rounded-xl flex justify-between items-center">
          <div>
            <p className="text-xs uppercase font-bold text-brand-300">Total Monthly Income</p>
            <p className="text-3xl font-extrabold text-white">${totalMonthlyIncome.toFixed(2)}</p>
          </div>
          <button
            type="button"
            onClick={() => setIsAdding(!isAdding)}
            className="flex items-center gap-2 px-3.5 py-2 bg-brand-600 hover:bg-brand-500 text-white rounded-lg text-sm font-medium transition-colors shadow-md"
          >
            <FaPlus className="w-3.5 h-3.5" />
            {isAdding ? "Cancel" : "Add Income Source"}
          </button>
        </div>

        {/* Form to add new income source */}
        {isAdding && (
          <form onSubmit={handleAddSubmit} className="p-4 bg-slate-800/80 border border-slate-700 rounded-xl flex flex-col gap-3">
            <h4 className="text-md font-bold text-slate-100">Add New Income Source</h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-300">Amount ($)</label>
                <input
                  type="number"
                  step="0.01"
                  value={newAmount}
                  onChange={(e) => setNewAmount(e.target.value)}
                  placeholder="e.g. 4500"
                  className="w-full mt-1 bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-sm focus:outline-none focus:border-brand-500"
                  required
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-300">Description</label>
                <input
                  type="text"
                  value={newDescription}
                  onChange={(e) => setNewDescription(e.target.value)}
                  placeholder="e.g. Software Engineer Salary"
                  className="w-full mt-1 bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-sm focus:outline-none focus:border-brand-500"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-300">Source</label>
                <select
                  value={newSource}
                  onChange={(e) => setNewSource(e.target.value)}
                  className="w-full mt-1 bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-sm focus:outline-none focus:border-brand-500"
                >
                  {incomeSources.map((src) => (
                    <option key={src} value={src}>{src.replace("_", " ")}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-300">Type</label>
                <select
                  value={newType}
                  onChange={(e) => setNewType(e.target.value)}
                  className="w-full mt-1 bg-slate-900 border border-slate-700 text-slate-100 px-3 py-2 rounded-lg text-sm focus:outline-none focus:border-brand-500"
                >
                  {incomeTypes.map((t) => (
                    <option key={t} value={t}>{t.replace("_", " ")}</option>
                  ))}
                </select>
              </div>
            </div>
            <div className="flex justify-end gap-2 mt-2">
              <button
                type="submit"
                className="px-4 py-1.5 bg-brand-600 hover:bg-brand-500 text-white rounded-lg text-sm font-semibold transition-colors shadow-md"
              >
                Save Income
              </button>
            </div>
          </form>
        )}

        {/* Existing incomes list */}
        <div className="flex flex-col gap-3 max-h-[45vh] overflow-y-auto pr-1">
          <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Current Income Sources</h4>
          {(!incomes || incomes.length === 0) ? (
            <p className="text-slate-400 text-sm italic text-center py-4">No income sources listed. Add one above!</p>
          ) : (
            incomes.map((inc) => (
              <div
                key={inc.incomeId}
                className="p-3 bg-slate-800/60 border border-slate-700/60 rounded-xl flex justify-between items-center"
              >
                {editingId === inc.incomeId ? (
                  <div className="flex-1 flex gap-2 items-center">
                    <input
                      type="number"
                      step="0.01"
                      value={editAmount}
                      onChange={(e) => setEditAmount(e.target.value)}
                      className="w-32 bg-slate-900 border border-brand-500 text-white px-2 py-1 rounded text-sm"
                    />
                    <input
                      type="text"
                      value={editDescription}
                      onChange={(e) => setEditDescription(e.target.value)}
                      placeholder="Description"
                      className="flex-1 bg-slate-900 border border-slate-700 text-white px-2 py-1 rounded text-sm"
                    />
                    <button
                      type="button"
                      onClick={() => handleSaveEdit(inc)}
                      className="px-3 py-1 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded"
                    >
                      Save
                    </button>
                    <button
                      type="button"
                      onClick={() => setEditingId(null)}
                      className="px-2 py-1 bg-slate-700 text-slate-300 text-xs rounded"
                    >
                      Cancel
                    </button>
                  </div>
                ) : (
                  <>
                    <div>
                      <p className="font-bold text-white text-lg">${inc.amount?.toFixed(2)}</p>
                      <p className="text-xs text-slate-400">
                        {inc.description || `${inc.incomeSource} - ${inc.incomeType}`}
                      </p>
                    </div>
                    <button
                      type="button"
                      onClick={() => handleStartEdit(inc)}
                      className="p-2 text-slate-400 hover:text-brand-300 hover:bg-slate-700/50 rounded-lg transition-colors"
                      title="Edit Income"
                    >
                      <FaPencilAlt className="w-4 h-4" />
                    </button>
                  </>
                )}
              </div>
            ))
          )}
        </div>

        <div className="flex justify-end pt-3 border-t border-slate-800">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors"
          >
            Done
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default EditIncomeModal;
