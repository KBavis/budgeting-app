import React from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";

/**
 * CategoryTypeSlider — Slider for setting percentage allocation of monthly income
 */
const CategoryTypeSlider = ({ categoryType, totalIncome = 0, onSliderChange }) => {
   const handleSliderChange = (value) => {
      const clampedValue = Math.max(0.01, Math.min(0.99, value));
      onSliderChange(categoryType.name, clampedValue);
   };

   const percentage = Math.round(categoryType.budgetAllocationPercentage * 100);
   const calculatedDollarTarget = totalIncome * categoryType.budgetAllocationPercentage;

   const getCategoryDescription = (name) => {
      switch (name.toLowerCase()) {
         case "needs":
            return "Essential living costs (rent, groceries, bills)";
         case "wants":
            return "Flexible spending (entertainment, dining out)";
         case "investments":
            return "Savings & retirement contributions";
         default:
            return "";
      }
   };

   return (
      <div className="flex flex-col gap-2 p-4 bg-slate-800/50 border border-slate-700/60 rounded-xl">
         <div className="flex justify-between items-center">
            <div>
               <h3 className="text-sm font-bold text-white tracking-wide">
                  {categoryType.name}
               </h3>
               <p className="text-[11px] text-slate-400">
                  {getCategoryDescription(categoryType.name)}
               </p>
            </div>
            <div className="text-right">
               <span className="text-sm font-bold text-brand-300">
                  {percentage}%
               </span>
               {totalIncome > 0 && (
                  <p className="text-[11px] font-semibold text-emerald-400">
                     ${calculatedDollarTarget.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}/mo
                  </p>
               )}
            </div>
         </div>

         <div className="pt-2 px-1">
            <Slider
               value={categoryType.budgetAllocationPercentage}
               onChange={handleSliderChange}
               min={0}
               max={1}
               step={0.01}
               handleStyle={{
                  borderColor: "#6366F1",
                  backgroundColor: "#818CF8",
                  boxShadow: "0 0 10px rgba(99, 102, 241, 0.5)",
               }}
               trackStyle={{
                  backgroundColor: "#6366F1",
               }}
               railStyle={{
                  backgroundColor: "#334155",
               }}
            />
         </div>
      </div>
   );
};

export default CategoryTypeSlider;
