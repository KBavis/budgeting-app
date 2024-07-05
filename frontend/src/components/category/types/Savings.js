import React, { useContext } from "react";
import categoryTypeContext from "../../../context/category/types/categoryTypeContext";

/**
 * Component that will display the difference in users allocated budget and their monthly income
 * @returns
 */
const Savings = () => {
  const { categoryTypes } = useContext(categoryTypeContext);

  // Calculate total saved amount from categoryTypes
  const totalSavedAmount = categoryTypes.reduce((total, categoryType) => {
    return total + categoryType.savedAmount;
  }, 0);

  return (
    <div className="bg-gray-800 text-white p-2 rounded-md shadow-sm w-1/3 md:w-1/4 lg:w-1/5 text-center mb-4 border-[2px] border-indigo-500">
      <div className="flex flex-col items-center">
        <h2 className="text-lg font-bold mb-1">
          Savings Per Month:{"    "}
          <span className="ml-4 text-2xl font-semibold text-green-500">
            ${totalSavedAmount.toFixed(2)}
          </span>
        </h2>
      </div>
    </div>
  );
};

export default Savings;
