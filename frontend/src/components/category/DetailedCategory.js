import React, { useState, useEffect, useContext } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import DetailedCategoryTransaction from "../transaction/DetailedCategoryTransaction";

/**
 * Component for representing a Category entitiy in detail
 *
 * @param category
 *       - Category to generate component for
 * @param handleShowSplitTransactionModal
 *       - functionality to show SplitTransaction modal
 * @param handleShowReduceTransactionModal
 *       - functionality to show ReduceTransaction modal
 * @param handleShowRenameTransactionModal
 *       - functionality to show RenameTransactionModal
 * @param handleShowAssignCategoryModal
 *       - functionlaity to show AssignCategoryModal
 * @returns
 */
const DetailedCategory = ({
  category,
  handleShowSplitTransactionModal,
  handleShowReduceTransactionModal,
  handleShowRenameTransactionModal,
  handleShowAssignCategoryModal,
}) => {
  const [totalAmountSpent, setTotalAmountSpent] = useState(0);
  const [budgetUsage, setBudgetUsage] = useState(0);
  const [filteredTransactions, setFilteredTransactions] = useState([]);

  const { transactions } = useContext(transactionContext);

  useEffect(() => {
    if (transactions) {
      const filtered = transactions.filter(
        (transaction) =>
          transaction.category &&
          transaction.category.categoryId === category.categoryId
      );

      setFilteredTransactions(filtered);

      const sum = filtered.reduce((acc, transaction) => {
        return acc + transaction.amount;
      }, 0);

      setTotalAmountSpent(sum.toFixed(0));
      setBudgetUsage((sum / category.budgetAmount) * 100);
    }
  }, [transactions, category.categoryId]);

  const getProgressBarColor = () => {
    const percentage = budgetUsage;
    if (percentage <= 50) {
      return "bg-green-500";
    } else if (percentage <= 70) {
      return "bg-yellow-500";
    } else if (percentage <= 90) {
      return "bg-orange-500";
    } else {
      return "bg-red-500";
    }
  };

  const getProgressTextColor = () => {
    const percentage = budgetUsage;
    if (percentage <= 50) {
      return "text-green-500";
    } else if (percentage <= 70) {
      return "text-yellow-500";
    } else if (percentage <= 90) {
      return "text-orange-500";
    } else {
      return "text-red-500";
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6 mx-4 w-full lg:w-2/3">
      <h3 className="text-2xl font-bold mb-5 text-center">{category.name}</h3>
      <div className="text-center mb-2 font-semibold text-xl">
        Spent{" "}
        <span className={`font-extrabold ${getProgressTextColor()}`}>
          {" "}
          ${totalAmountSpent}{" "}
        </span>{" "}
        out of allocated
        <span className="text-black font-bold">
          {" "}
          ${category.budgetAmount.toFixed(0)}
        </span>
      </div>
      <div className="w-full bg-gray-300 rounded-full h-4 mb-4">
        <div
          className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()}`}
          style={{ width: `${budgetUsage > 100 ? 100 : budgetUsage}%` }}
        ></div>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 max-h-64 overflow-y-auto">
        {filteredTransactions.map((transaction) => (
          <DetailedCategoryTransaction
            key={transaction.transactionId}
            transaction={transaction}
            handleShowSplitTransactionModal={handleShowSplitTransactionModal}
            handleShowReduceTransactionModal={handleShowReduceTransactionModal}
            handleShowRenameTransactionModal={handleShowRenameTransactionModal}
            handleShowAssignCategoryModal={handleShowAssignCategoryModal}
          />
        ))}
      </div>
    </div>
  );
};

export default DetailedCategory;
