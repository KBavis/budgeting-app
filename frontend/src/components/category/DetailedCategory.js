import React, { useState, useEffect, useContext } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import DetailedCategoryTransaction from "../transaction/DetailedCategoryTransaction";

/**
 * Component to store Category component on CategoryTypes page
 *
 * @param category
 */
const DetailedCategory = ({ category }) => {
  // Local State
  const [totalAmountSpent, setTotalAmountSpent] = useState(0);
  const [budgetUsage, setBudgetUsage] = useState(0);
  const [filteredTransactions, setFilteredTransactions] = useState([]);

  // Global State
  const { transactions } = useContext(transactionContext);
  useEffect(() => {
    if (transactions) {
      // Filter Transactions corresponding to current Category
      const filtered = transactions.filter(
        (transaction) =>
          transaction.category &&
          transaction.category.categoryId === category.categoryId
      );

      setFilteredTransactions(filtered);

      // Sum total amount of Transactions corresponding to entity
      const sum = filtered.reduce((acc, transaction) => {
        return acc + transaction.amount;
      }, 0);

      // Set Total Amount & Budget Usage Percentage
      setTotalAmountSpent(sum.toFixed(0));
      setBudgetUsage((sum / category.budgetAmount) * 100);
    }
  }, [transactions, category.categoryId]);

  // Function to determine progress bar for our Category entity
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

  //Function to determine progress color for our Spent amount
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
    <div className="bg-white rounded-lg shadow-md p-4 w-1/3 mx-2">
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
      <div className="overflow-y-auto max-h-64 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 ">
        {filteredTransactions.map((transaction) => (
          <DetailedCategoryTransaction
            key={transaction.transactionId}
            transaction={transaction}
          />
        ))}
      </div>
    </div>
  );
};

export default DetailedCategory;
