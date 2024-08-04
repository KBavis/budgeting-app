import React, { useContext } from "react";
import categoryTypeContext from "../../../context/category/types/categoryTypeContext";

/**
 * Component that displays the expected savings for the current month based on budget allocations.
 * @returns {JSX.Element} The Savings component.
 */
const Savings = () => {
  const { categoryTypes } = useContext(categoryTypeContext);

  // Calculate total saved amount from categoryTypes
  const totalSavedAmount = categoryTypes.reduce((total, categoryType) => {
    return total + categoryType.savedAmount;
  }, 0);

  return (
      <div className="bg-gray-800 text-white p-4 rounded-md shadow-md w-full md:w-2/3 lg:w-1/4 text-center mb-6 border-2 border-indigo-500">
        <div className="flex flex-col items-center">
          <h2 className="text-lg font-bold mb-2">
            Your Expected Savings for This Month:
          </h2>
          <span className="text-2xl font-semibold text-green-500">
          ${totalSavedAmount.toFixed(2)}
        </span>
        </div>
      </div>
  );
};

export default Savings;;
