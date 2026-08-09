import React, { useContext } from "react";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import categoryContext from "../../context/category/categoryContext";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";
import Modal from "../layout/Modal";
import { ThemeContext } from "../../context/theme/ThemeContext";

const AssignCategoryModal = ({ onClose, transaction }) => {
  // Global State
  const { categoryTypes } = useContext(categoryTypeContext);
  const { categories } = useContext(categoryContext);
  const { setAlert } = useContext(AlertContext);
  const { updateCategory } = useContext(transactionContext);
  const { theme } = useContext(ThemeContext);

  const isDark = theme === "dark";

  // Helper to reliably retrieve all categories for a category type
  const getCategoriesForType = (ct) => {
    const globalCats = (categories || []).filter(
      (c) => c.categoryTypeId === ct.categoryTypeId
    );
    const typeCats = ct.categories || [];
    const map = new Map();
    [...typeCats, ...globalCats].forEach((c) => {
      if (c && c.categoryId) map.set(c.categoryId, c);
    });
    return Array.from(map.values());
  };

  // Function to handle category selection
  const handleCategorySelection = (category) => {
    if (!category) {
      setAlert("Please select a category.", "danger");
      return;
    }

    updateCategory(transaction.transactionId, category.categoryId);
    setAlert("Category successfully assigned", "success");
    onClose();
  };

  return (
    <Modal isOpen={true} onClose={onClose} title="Assign Category" size="lg">
      <div className="flex flex-col gap-4">
        <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-600"}`}>
          Select a category to assign to the transaction.
        </p>

        <div className={`flex items-center gap-3 p-3.5 border rounded-xl ${
            isDark ? "bg-slate-800/80 border-slate-700/50" : "bg-slate-50 border-slate-200"
        }`}>
          <img
            src={
              transaction.logoUrl ||
              "https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
            }
            alt="Transaction Logo"
            className="w-10 h-10 rounded-full object-cover border border-slate-300 dark:border-slate-600"
          />
          <div>
            <p className={`font-bold ${isDark ? "text-slate-100" : "text-slate-900"}`}>{transaction.name}</p>
            <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-500"}`}>${transaction.amount}</p>
          </div>
        </div>

        <div className="flex flex-col gap-4 max-h-[55vh] overflow-y-auto pr-1">
          {categoryTypes?.map((ct) => {
            const catList = getCategoriesForType(ct);
            return (
              <div
                key={ct.categoryTypeId}
                className={`p-4 border rounded-xl flex flex-col gap-3 ${
                  isDark ? "bg-slate-800/50 border-slate-700/60" : "bg-slate-50 border-2 border-slate-300"
                }`}
              >
                <h3 className="text-sm font-bold text-brand-600 dark:text-brand-400 uppercase tracking-wider">
                  {ct.name}
                </h3>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                  {catList?.map((cat) => (
                    <button
                      key={cat.categoryId}
                      type="button"
                      onClick={() => handleCategorySelection(cat)}
                      className={`px-3 py-2 rounded-lg text-sm font-bold transition-all border text-left truncate ${
                        isDark
                          ? "bg-slate-900 border-slate-700 text-slate-200 hover:bg-brand-600 hover:border-brand-500 hover:text-white"
                          : "bg-white border-2 border-slate-300 text-slate-900 hover:bg-brand-600 hover:border-brand-600 hover:text-white shadow-sm"
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

        <div className={`flex justify-end pt-3 border-t ${isDark ? "border-slate-800" : "border-slate-200"}`}>
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
        </div>
      </div>
    </Modal>
  );
};

export default AssignCategoryModal;
