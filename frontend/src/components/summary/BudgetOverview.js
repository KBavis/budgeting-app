import React from "react";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";

/**
 * Component used to represent a BudgetOverview in our BudgetPerformance entity
 *
 * @param overview
 *          - BudgetOverview to generate component for
 * @returns
 *          - BudgetOverview component
 */
const BudgetOverview = ({ overview }) => {
   const {
      overviewType,
      totalSpent,
      totalAmountAllocated,
      totalPercentUtilized,
      totalAmountSaved,
      savedAmountAttributesTotal,
   } = overview;

   // If over budget, make the spent percentage 100% for pie chart display
   const adjustedTotalSpent =
      totalSpent > totalAmountAllocated ? totalAmountAllocated : totalSpent;

   const data = [
      { name: "Spent", value: parseFloat(adjustedTotalSpent.toFixed(2)) },
      {
         name: "Remaining",
         value: parseFloat(
            Math.max(totalAmountAllocated - adjustedTotalSpent, 0).toFixed(2)
         ),
      },
   ];

   const COLORS = ["#4f46e5", "#d1d5db"]; // Indigo for spent, light gray for remaining

   const getProgressBarColor = () => {
      const percentage = totalPercentUtilized * 100;
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

   const getSpentColor = () => {
      const percentage = totalPercentUtilized * 100;
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

   const convertToNormalCase = (str) => {
      return str
         .toLowerCase()
         .split(" ")
         .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
         .join(" ");
   };

   const getTextColor = (value) => {
      return value >= 0 ? "text-green-500" : "text-red-500";
   };

   const getOverUnderText = (value) => {
      return value >= 0 ? "Under" : "Over";
   };

   return (
      <div className="relative bg-white rounded-lg shadow-md p-6 mx-4 w-full lg:w-2/3 mb-14">
         <h3 className="text-2xl font-bold mb-5 text-center">
            {convertToNormalCase(overviewType)} Overview
         </h3>
         <div className="text-center mb-2 font-semibold text-xl ">
            Spent{" "}
            <span className={`font-extrabold ${getSpentColor()}`}>
               ${totalSpent.toFixed(2)}
            </span>{" "}
            out of allocated
            <span className="text-black font-bold">
               {" "}
               ${totalAmountAllocated.toFixed(2)}
            </span>
         </div>
         <div className="w-full bg-gray-300 rounded-full h-4 mb-4">
            <div
               className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()}`}
               style={{
                  width: `${
                     totalPercentUtilized * 100 > 100
                        ? 100
                        : totalPercentUtilized * 100
                  }%`,
               }}
            ></div>
         </div>
         <div className="text-center font-semibold text-lg mb-4">
            Budget Utilization:{" "}
            <span className="font-bold">
               {(totalPercentUtilized * 100).toFixed(2)}%
            </span>
         </div>
         <div className="text-center font-semibold text-lg mb-4">
            Amount {getOverUnderText(savedAmountAttributesTotal)} Budget:{" "}
            <span
               className={`font-bold ${getTextColor(
                  savedAmountAttributesTotal
               )}`}
            >
               ${Math.abs(savedAmountAttributesTotal).toFixed(2)}
            </span>
         </div>
         <div className="text-center font-semibold text-lg mb-4">
            Total Amount Saved:{" "}
            <span className={`font-bold ${getTextColor(totalAmountSaved)}`}>
               ${totalAmountSaved.toFixed(2)}
            </span>
         </div>
         <ResponsiveContainer width="100%" height={200}>
            <PieChart>
               <Pie
                  data={data}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
               >
                  {data.map((entry, index) => (
                     <Cell
                        key={`cell-${index}`}
                        fill={COLORS[index % COLORS.length]}
                     />
                  ))}
               </Pie>
               <Tooltip />
            </PieChart>
         </ResponsiveContainer>
      </div>
   );
};

export default BudgetOverview;
