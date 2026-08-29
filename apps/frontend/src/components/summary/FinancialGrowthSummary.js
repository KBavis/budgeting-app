import React, { useContext } from "react";
import { FaPiggyBank, FaSeedling, FaExclamationTriangle } from "react-icons/fa";
import { ThemeContext } from "../../context/theme/ThemeContext";

/**
 * FinancialGrowthSummary — Executive financial overview component for historical budget summaries.
 *
 * Shows:
 *  - Net Wealth Built hero card (Invested + Cash Flow)
 *  - Monthly Cash Flow grid (Total Income, Total Spent, Cash Surplus/Deficit)
 *  - Total Spent Breakdown grid (Needs Spending, Wants Spending, Total Invested)
 */
const FinancialGrowthSummary = ({ summary }) => {
   const { theme } = useContext(ThemeContext);
   const isDark = theme === "dark";

   const generalOverview = summary?.generalOverview || {};
   const investmentOverview = summary?.investmentOverview || {};
   const needsOverview = summary?.needsOverview || {};
   const wantsOverview = summary?.wantsOverview || {};

   // Core values
   const totalIncome = generalOverview.totalAmountAllocated || 0;
   const totalSpent = generalOverview.totalSpent || 0;
   const investedAmount = investmentOverview.totalSpent || 0;
   const needsSpent = needsOverview.totalSpent || 0;
   const wantsSpent = wantsOverview.totalSpent || 0;

   // Cash flow: income minus total spending
   const netCashFlow = totalIncome - totalSpent;
   const isCashDeficit = netCashFlow < 0;

   // Net Wealth Built = Investments + Cash Flow
   // Investments grow your assets, cash surplus stays in bank, cash deficit means debt
   const netWealthBuilt = investedAmount + netCashFlow;
   const isNetWealthNegative = netWealthBuilt < 0;

   const formatCurrency = (val) => {
      return "$" + Math.abs(val).toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
   };

   const formatCompact = (val) => {
      return "$" + Math.abs(val).toLocaleString("en-US", { minimumFractionDigits: 0, maximumFractionDigits: 0 });
   };

   return (
      <div className={`relative rounded-2xl shadow-lg p-4 sm:p-6 mx-auto w-full max-w-4xl mb-8 border transition-all ${isDark
         ? "bg-slate-900/80 border-slate-700/60 text-slate-100"
         : "bg-white border-slate-200 text-slate-800"
         }`}>

         {/* Component Header */}
         <div className="flex items-center justify-center gap-2.5 mb-4 sm:mb-5">
            <div className={`p-2 rounded-xl border ${isDark
               ? "bg-teal-500/15 border-teal-500/30 text-teal-400"
               : "bg-teal-50 border-teal-200 text-teal-600"
               }`}>
               <FaSeedling size={16} />
            </div>
            <h3 className={`text-lg sm:text-xl font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
               Financial Growth Summary
            </h3>
         </div>

         {/* Hero Banner Card — Net Wealth Built */}
         <div className={`text-center mb-5 sm:mb-6 p-3.5 sm:p-4 rounded-xl border transition-all ${isNetWealthNegative
            ? isDark ? "bg-red-500/10 border-red-500/30" : "bg-red-50 border-red-200"
            : isDark ? "bg-teal-500/10 border-teal-500/30" : "bg-teal-50 border-teal-200"
            }`}>
            <p className={`text-[10px] sm:text-xs font-extrabold uppercase tracking-wider mb-1 flex items-center justify-center gap-1.5 ${isNetWealthNegative
               ? isDark ? "text-red-400" : "text-red-600"
               : isDark ? "text-teal-400" : "text-teal-600"
               }`}>
               {isNetWealthNegative ? <FaExclamationTriangle size={11} /> : <FaPiggyBank size={11} />}
               {isNetWealthNegative ? "Net Wealth Loss" : "Net Wealth Built"}
            </p>
            <p className={`text-2xl sm:text-3xl font-black ${isNetWealthNegative
               ? isDark ? "text-red-400" : "text-red-600"
               : isDark ? "text-teal-400" : "text-teal-600"
               }`}>
               {isNetWealthNegative ? "-" : ""}{formatCurrency(netWealthBuilt)}
            </p>
            <p className={`text-[10px] sm:text-xs font-medium mt-1 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
               {formatCompact(investedAmount)} Invested {isCashDeficit ? "−" : "+"} {formatCompact(netCashFlow)} {isCashDeficit ? "Deficit" : "Surplus"}
            </p>
         </div>

         {/* Row 1: Cash Flow — 3-column grid */}
         <div className="mb-4 sm:mb-5">
            <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-2 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
               Monthly Cash Flow
            </p>
            <div className="grid grid-cols-3 gap-2 sm:gap-3 text-center">
               {/* Total Income */}
               <div className={`p-3 sm:p-3.5 rounded-xl border ${isDark ? "bg-slate-800/60 border-slate-700/60" : "bg-slate-50 border-slate-200"}`}>
                  <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                     Total Income
                  </p>
                  <p className={`text-sm sm:text-base font-extrabold ${isDark ? "text-white" : "text-slate-900"}`}>
                     {formatCurrency(totalIncome)}
                  </p>
               </div>

               {/* Total Spent */}
               <div className={`p-3 sm:p-3.5 rounded-xl border ${isDark ? "bg-slate-800/60 border-slate-700/60" : "bg-slate-50 border-slate-200"}`}>
                  <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                     Total Spent
                  </p>
                  <p className={`text-sm sm:text-base font-extrabold ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                     {formatCurrency(totalSpent)}
                  </p>
               </div>

               {/* Cash Surplus / Deficit */}
               <div className={`p-3 sm:p-3.5 rounded-xl border ${isCashDeficit
                  ? isDark ? "bg-red-500/10 border-red-500/30" : "bg-red-50 border-red-200"
                  : isDark ? "bg-emerald-500/10 border-emerald-500/30" : "bg-emerald-50 border-emerald-200"
                  }`}>
                  <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isCashDeficit
                     ? isDark ? "text-red-400" : "text-red-600"
                     : isDark ? "text-emerald-400" : "text-emerald-600"
                     }`}>
                     {isCashDeficit ? "Cash Deficit" : "Cash Surplus"}
                  </p>
                  <p className={`text-sm sm:text-base font-black ${isCashDeficit
                     ? isDark ? "text-red-400" : "text-red-600"
                     : isDark ? "text-emerald-400" : "text-emerald-600"
                     }`}>
                     {isCashDeficit ? "-" : "+"}{formatCurrency(netCashFlow)}
                  </p>
               </div>
            </div>
         </div>

         {/* Row 2: Total Spent Breakdown — 3-column grid showing where Total Spent went */}
         <div>
            <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-2 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
               Total Spent Breakdown
            </p>
            <div className="grid grid-cols-3 gap-2 sm:gap-3 text-center">
               {/* Needs Spending */}
               <div className={`p-3 sm:p-3.5 rounded-xl border ${isDark ? "bg-slate-800/60 border-slate-700/60" : "bg-slate-50 border-slate-200"}`}>
                  <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isDark ? "text-blue-400" : "text-blue-600"}`}>
                     Needs Spending
                  </p>
                  <p className={`text-sm sm:text-base font-black ${isDark ? "text-blue-400" : "text-blue-600"}`}>
                     {formatCurrency(needsSpent)}
                  </p>
               </div>

               {/* Wants Spending */}
               <div className={`p-3 sm:p-3.5 rounded-xl border ${isDark ? "bg-slate-800/60 border-slate-700/60" : "bg-slate-50 border-slate-200"}`}>
                  <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isDark ? "text-purple-400" : "text-purple-600"}`}>
                     Wants Spending
                  </p>
                  <p className={`text-sm sm:text-base font-black ${isDark ? "text-purple-400" : "text-purple-600"}`}>
                     {formatCurrency(wantsSpent)}
                  </p>
               </div>

               {/* Total Invested */}
               <div className={`p-3 sm:p-3.5 rounded-xl border ${isDark ? "bg-indigo-500/10 border-indigo-500/25" : "bg-indigo-50 border-indigo-200"}`}>
                  <p className={`text-[9px] sm:text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isDark ? "text-indigo-400" : "text-indigo-600"}`}>
                     Total Invested
                  </p>
                  <p className={`text-sm sm:text-base font-black ${isDark ? "text-indigo-400" : "text-indigo-600"}`}>
                     {formatCurrency(investedAmount)}
                  </p>
               </div>
            </div>
         </div>
      </div>
   );
};

export default FinancialGrowthSummary;
