import React from "react";

/**
 * Centralized Registration Step Progress Bar
 * Displays "Step X of Y" with step title, optional subtitle, and animated progress segments.
 */
const StepProgress = ({
   currentStep = 1,
   totalSteps = 4,
   subTitle,
   steps = ["Register", "Connect Accounts", "Income", "Allocations", "Categories"]
}) => {
   return (
      <div className="w-full max-w-xl mb-6 animate-fade-in">
         <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
               Step {currentStep} of {totalSteps} {subTitle ? `— ${subTitle}` : ""}
            </span>
            <span className="text-xs font-semibold text-indigo-400">
               {steps[currentStep - 1] || ""}
            </span>
         </div>
         <div className="flex gap-1.5">
            {Array.from({ length: totalSteps }).map((_, i) => (
               <div
                  key={i}
                  className={`h-1.5 flex-1 rounded-full transition-all duration-500 ${
                     i < currentStep
                        ? "bg-gradient-to-r from-indigo-500 to-violet-500 shadow-sm"
                        : "bg-slate-700/60"
                  }`}
               />
            ))}
         </div>
      </div>
   );
};

export default StepProgress;
