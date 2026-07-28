import React, { useContext, useState } from "react";
import { FaPlus, FaTimes } from "react-icons/fa";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";

const SplitTransactionModal = ({ onClose, transaction, presetSplits }) => {
  const { setAlert } = useContext(AlertContext);
  const { splitTransaction } = useContext(transactionContext);
  const { theme } = useContext(ThemeContext);

  const isDark = theme === "dark";

  const [splitTransactions, setSplitTransactions] = useState(() => {
    if (presetSplits && Array.isArray(presetSplits) && presetSplits.length >= 2) {
      return presetSplits.map((s) => ({
        name: s.name || transaction?.name || "",
        amount: s.amount ? s.amount.toString() : "",
        removable: true,
      }));
    }
    return [
      { name: `${transaction?.name || ""} (Part 1)`, amount: "", removable: false },
      { name: `${transaction?.name || ""} (Part 2)`, amount: "", removable: false },
    ];
  });

  const handleAddSplitTransaction = () => {
    setSplitTransactions([
      ...splitTransactions,
      { name: "", amount: "", removable: true },
    ]);
  };

  const handleRemoveSplitTransaction = (index) => {
    if (splitTransactions[index].removable || splitTransactions.length > 2) {
      const updatedTransactions = [...splitTransactions];
      updatedTransactions.splice(index, 1);
      setSplitTransactions(updatedTransactions);
    }
  };

  const handleInputChange = (index, event) => {
    const { name, value } = event.target;
    const list = [...splitTransactions];
    list[index][name] = value;
    setSplitTransactions(list);
  };

  const onConfirm = () => {
    const filledTransactions = splitTransactions.filter(
      (split) => split.name && split.amount
    );
    if (filledTransactions.length < 2) {
      setAlert("Please ensure at least two transactions are split out.", "danger");
      return;
    }

    const totalAmount = filledTransactions.reduce(
      (total, split) => total + parseFloat(split.amount),
      0
    );

    if (totalAmount > parseFloat(transaction.amount)) {
      setAlert(
        "The total amount of split-out transactions cannot exceed the original transaction amount.",
        "danger"
      );
      return;
    }

    const transactionDtos = filledTransactions.map((split) => ({
      updatedName: split.name,
      updatedAmount: parseFloat(split.amount),
    }));

    splitTransaction(transaction.transactionId, { splitTransactions: transactionDtos });
    setAlert("Transaction split successfully", "success");
    onClose();
  };

  return (
    <Modal isOpen={true} onClose={onClose} title="Split Transaction" size="md">
      <div className="flex flex-col gap-4">
        <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
          Divide transaction amount (${transaction?.amount}) into separate portions.
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

        <div className="flex flex-col gap-3 max-h-[50vh] overflow-y-auto pr-1">
          {splitTransactions.map((split, index) => (
            <div
              key={index}
              className={`relative p-3 border rounded-xl flex flex-col gap-2 ${
                isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-slate-200"
              }`}
            >
              {(split.removable || splitTransactions.length > 2) && (
                <button
                  type="button"
                  className="absolute top-2.5 right-2.5 p-1 text-slate-400 hover:text-red-400 transition-colors"
                  onClick={() => handleRemoveSplitTransaction(index)}
                >
                  <FaTimes className="w-3.5 h-3.5" />
                </button>
              )}
              <input
                type="text"
                name="name"
                value={split.name}
                onChange={(e) => handleInputChange(index, e)}
                placeholder="Transaction Name"
                className={`w-full px-3 py-2 rounded-lg text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                  isDark ? "bg-slate-900 border border-slate-700 text-slate-100" : "bg-white border border-slate-300 text-slate-900"
                }`}
              />
              <div className="relative flex items-center">
                <span className={`absolute left-3 text-xs ${isDark ? "text-slate-400" : "text-slate-500"}`}>$</span>
                <input
                  type="number"
                  step="0.01"
                  name="amount"
                  value={split.amount}
                  onChange={(e) => handleInputChange(index, e)}
                  placeholder="Transaction Amount"
                  className={`w-full pl-7 pr-3 py-2 rounded-lg text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                    isDark ? "bg-slate-900 border border-slate-700 text-slate-100" : "bg-white border border-slate-300 text-slate-900"
                  }`}
                />
              </div>
            </div>
          ))}
        </div>

        <button
          type="button"
          onClick={handleAddSplitTransaction}
          className="self-center flex items-center justify-center w-9 h-9 bg-brand-600 hover:bg-brand-500 text-white rounded-full transition-colors shadow-md"
          title="Add another split portion"
        >
          <FaPlus className="w-4 h-4" />
        </button>

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

export default SplitTransactionModal;
