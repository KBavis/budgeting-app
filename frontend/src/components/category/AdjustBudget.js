import React from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import { FaTimes } from "react-icons/fa";

/**
 * AdjustBudget — Component for adjusting sliders for selected categories within a bucket
 */
const AdjustBudget = ({
   categories,
   onSliderChange,
   onRemoveCategory,
   totalBudget,
   remainingBudget,
}) => {
   const isOverBudget = remainingBudget < 0;

   return (
      <div className="mb-6 flex flex-col gap-4 text-left">
         {/* Total Allocated Budget Info Banner */}
         <div className="p-4 bg-slate-800/90 border border-slate-700/80 rounded-xl flex justify-between items-center shadow-inner">
            <div>
               <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                  Target Budget Pool
               </p>
               <p className="text-lg font-bold text-white">
                  ${totalBudget.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
               </p>
            </div>
            <div className="text-right">
               <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                  Unallocated Remaining
               </p>
               <p
                  className={`text-lg font-bold ${
                     isOverBudget ? "text-red-400" : "text-emerald-400"
                  }`}
               >
                  ${remainingBudget.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
               </p>
            </div>
         </div>

         {/* Savings Impact Callout */}
         <p className="text-[10px] text-amber-300/90 bg-amber-500/10 px-2.5 py-1.5 rounded-lg border border-amber-500/20">
            💡 <strong>Note:</strong> Unallocated/unspent budget is tracked as <strong>Savings</strong> (overspending reduces savings).
         </p>

         {/* Category Sliders List */}
         {categories.length > 0 ? (
            <div className="flex flex-col gap-3 max-h-72 overflow-y-auto pr-1">
               {categories.map((category) => {
                  const pct = Math.round(category.budgetAllocationPercentage * 100);
                  const dollarAmount = category.budgetAmount || 0;

                  return (
                     <div
                        key={category.name}
                        className="p-3 bg-slate-800/50 border border-slate-700/60 rounded-xl flex flex-col gap-2 relative group"
                     >
                        <div className="flex justify-between items-center">
                           <span className="text-xs font-bold text-slate-100">
                              {category.name}
                           </span>
                           <div className="flex items-center gap-2">
                              <span className="text-xs font-semibold text-brand-300">
                                 {pct}% (${dollarAmount.toFixed(2)})
                              </span>
                              <button
                                 type="button"
                                 onClick={() => onRemoveCategory(category.name)}
                                 className="p-1 text-slate-400 hover:text-red-400 rounded-md hover:bg-slate-700 transition-colors"
                                 title="Remove category"
                              >
                                 <FaTimes className="w-3 h-3" />
                              </button>
                           </div>
                        </div>

                        <div className="px-1 pt-1">
                           <Slider
                              value={category.budgetAllocationPercentage}
                              min={0}
                              max={1}
                              step={0.01}
                              onChange={(value) =>
                                 onSliderChange(category.name, value, totalBudget)
                              }
                              handleStyle={{
                                 borderColor: "#6366F1",
                                 backgroundColor: "#818CF8",
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
               })}
            </div>
         ) : (
            <div className="py-6 text-center text-xs text-slate-500 border border-dashed border-slate-700 rounded-xl">
               No categories added yet. Choose from suggested options above or add your own!
            </div>
         )}
      </div>
   );
};

export default AdjustBudget;
