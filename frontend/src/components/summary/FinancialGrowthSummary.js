import React, { useContext, useEffect, useState } from "react";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";
import { useNavigate } from "react-router-dom";
import { FaChartLine, FaArrowRight, FaPiggyBank, FaSeedling, FaExclamationTriangle } from "react-icons/fa";
import CategoryPerformanceContext from "../../context/category/performances/categoryPerformanceContext";
import categoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import { ThemeContext } from "../../context/theme/ThemeContext";

/**
 * FinancialGrowthSummary — replaces General + Investments BudgetOverview cards.
 *
 * Shows:
 *  - Net worth accumulated (investments + savings from underspending needs/wants)
 *  - Invested amount
 *  - Savings from underspending needs/wants
 *  - Pie chart of investment category breakdown
 */
const FinancialGrowthSummary = ({ summary, month, year }) => {
   const navigate = useNavigate();
   const [investmentPieData, setInvestmentPieData] = useState([]);
   const [categoryMap, setCategoryMap] = useState({});
   const [investmentTypeId, setInvestmentTypeId] = useState(null);

   const { category_performances } = useContext(CategoryPerformanceContext);
   const { categories, fetchCategories } = useContext(categoryContext);
   const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const generalOverview = summary?.generalOverview || {};
   const investmentOverview = summary?.investmentOverview || {};
   const needsOverview = summary?.needsOverview || {};
   const wantsOverview = summary?.wantsOverview || {};

   // Core calculations
   const investedAmount = investmentOverview.totalSpent || 0;
   const needsSaved = (needsOverview.totalAmountAllocated || 0) - (needsOverview.totalSpent || 0);
   const wantsSaved = (wantsOverview.totalAmountAllocated || 0) - (wantsOverview.totalSpent || 0);
   const expenseSavings = Math.max(0, needsSaved) + Math.max(0, wantsSaved);
   const expenseOverspent = (needsSaved < 0 ? Math.abs(needsSaved) : 0) + (wantsSaved < 0 ? Math.abs(wantsSaved) : 0);
   const netWealthBuilt = investedAmount + expenseSavings - expenseOverspent;

   const expenseAllocated = (needsOverview.totalAmountAllocated || 0) + (wantsOverview.totalAmountAllocated || 0);
   const expenseSpent = (needsOverview.totalSpent || 0) + (wantsOverview.totalSpent || 0);

   // fetch category types if page is refreshed
   useEffect(() => {
      if (!categoryTypes || categoryTypes.length === 0) {
         fetchCategoryTypes();
      }
   }, [categoryTypes]);

   // identify the Investments category type ID
   useEffect(() => {
      if (!categoryTypes) return;
      const invType = categoryTypes.find((t) => t.name.toLowerCase() === "investments");
      if (invType) {
         setInvestmentTypeId(invType.categoryTypeId);
      }
   }, [categoryTypes]);

   // generate category mapping
   useEffect(() => {
      if (!categories || categories.length === 0) {
         fetchCategories();
         return;
      }
      const mapping = {};
      categories.forEach((cat) => {
         mapping[cat.categoryId] = cat.name;
      });
      setCategoryMap(mapping);
   }, [categories]);

   // build pie chart data from investment category performances
   useEffect(() => {
      if (!category_performances || category_performances.length === 0 || investmentTypeId == null) {
         return;
      }

      const filtered = category_performances
         .filter((cp) => !cp.categoryTypeId || String(cp.categoryTypeId) === String(investmentTypeId))
         .filter((cp) => (cp.totalSpend || 0) > 0);

      const data = filtered.map((cp) => ({
         name: categoryMap[cp.categoryId] || "Unknown",
         value: parseFloat((cp.totalSpend || 0).toFixed(2)),
      }));
      setInvestmentPieData(data);
   }, [category_performances, investmentTypeId, categoryMap]);

   const DARK_COLORS = [
      "#818cf8", "#34d399", "#fbbf24", "#f472b6", "#38bdf8",
      "#a78bfa", "#4ade80", "#f97316", "#fb7185", "#2dd4bf",
   ];

   const LIGHT_COLORS = [
      "#4f46e5", "#059669", "#d97706", "#ec4899", "#0284c7",
      "#7c3aed", "#16a34a", "#ea580c", "#e11d48", "#0d9488",
   ];

   const activeColors = isDark ? DARK_COLORS : LIGHT_COLORS;

   const isNegative = netWealthBuilt < 0;

   const formatCurrency = (val) => {
      return "$" + Math.abs(val).toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
   };

   return (
      <div className={`relative rounded-2xl shadow-lg p-6 mx-auto w-full max-w-4xl mb-8 border transition-all ${isDark
         ? "bg-slate-900/80 border-slate-700/60 text-slate-100"
         : "bg-white border-slate-200 text-slate-800"
         }`}>

         {/* Header */}
         <div className="flex items-center justify-center gap-2.5 mb-5">
            <div className={`p-2 rounded-xl border ${isDark
               ? "bg-teal-500/15 border-teal-500/30 text-teal-400"
               : "bg-teal-50 border-teal-200 text-teal-600"
               }`}>
               <FaSeedling size={16} />
            </div>
            <h3 className={`text-xl font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
               Financial Growth Summary
            </h3>
         </div>

         {/* Hero Stat — Net Wealth Built */}
         <div className={`text-center mb-5 p-4 rounded-xl border ${isNegative
            ? isDark ? "bg-red-500/10 border-red-500/30" : "bg-red-50 border-red-200"
            : isDark ? "bg-teal-500/10 border-teal-500/30" : "bg-teal-50 border-teal-200"
            }`}>
            <p className={`text-xs font-extrabold uppercase tracking-wider mb-1 flex items-center justify-center gap-1.5 ${isNegative
               ? isDark ? "text-red-400" : "text-red-600"
               : isDark ? "text-teal-400" : "text-teal-600"
               }`}>
               {isNegative ? <FaExclamationTriangle size={11} /> : <FaPiggyBank size={11} />}
               {isNegative ? "Net Wealth Loss" : "Net Wealth Built"}
            </p>
            <p className={`text-3xl font-black ${isNegative
               ? isDark ? "text-red-400" : "text-red-600"
               : isDark ? "text-teal-400" : "text-teal-600"
               }`}>
               {isNegative ? "-" : ""}{formatCurrency(netWealthBuilt)}
            </p>
            <p className={`text-xs font-medium mt-1 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
               Investments made plus living expense savings (minus overspending)
            </p>
         </div>

         {/* 3-Column Breakdown */}
         <div className="grid grid-cols-3 gap-4 text-center mb-5">
            {/* Invested */}
            <div className={`p-3 rounded-xl border ${isDark ? "bg-slate-800/60 border-slate-700/60" : "bg-slate-50 border-slate-200"}`}>
               <p className={`text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isDark ? "text-indigo-400" : "text-indigo-600"}`}>
                  Invested
               </p>
               <p className={`text-lg font-black ${isDark ? "text-indigo-400" : "text-indigo-600"}`}>
                  {formatCurrency(investedAmount)}
               </p>
            </div>

            {/* Expense Savings */}
            <div className={`p-3 rounded-xl border ${isDark ? "bg-slate-800/60 border-slate-700/60" : "bg-slate-50 border-slate-200"}`}>
               <p className={`text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                  Expense Savings
               </p>
               <p className={`text-lg font-black ${expenseSavings > 0
                  ? isDark ? "text-emerald-400" : "text-emerald-600"
                  : isDark ? "text-slate-400" : "text-slate-500"
                  }`}>
                  {formatCurrency(expenseSavings)}
               </p>
            </div>

            {/* Expense Overspent */}
            <div className={`p-3 rounded-xl border ${isDark ? "bg-slate-800/60 border-slate-700/60" : "bg-slate-50 border-slate-200"}`}>
               <p className={`text-[10px] font-extrabold uppercase tracking-wider mb-0.5 ${expenseOverspent > 0
                  ? isDark ? "text-red-400" : "text-red-600"
                  : isDark ? "text-slate-500" : "text-slate-400"
                  }`}>
                  Expense Overspent
               </p>
               <p className={`text-lg font-black ${expenseOverspent > 0
                  ? isDark ? "text-red-400" : "text-red-600"
                  : isDark ? "text-slate-400" : "text-slate-500"
                  }`}>
                  {expenseOverspent > 0 ? "-" : ""}{formatCurrency(expenseOverspent)}
               </p>
            </div>
         </div>

         {/* Overall Living Expense Context */}
         <div className={`flex justify-between items-center px-3.5 py-2.5 rounded-xl mb-5 text-xs font-semibold ${isDark ? "bg-slate-800/50 text-slate-400" : "bg-slate-100 text-slate-500"
            }`}>
            <span>Needs & Wants Spent: <span className={`font-bold ${isDark ? "text-white" : "text-slate-800"}`}>{formatCurrency(expenseSpent)}</span> of <span className={`font-bold ${isDark ? "text-white" : "text-slate-800"}`}>{formatCurrency(expenseAllocated)}</span></span>
            <span>Total Invested: <span className={`font-bold ${isDark ? "text-indigo-400" : "text-indigo-600"}`}>{formatCurrency(investedAmount)}</span></span>
         </div>

         {/* Investment Category Breakdown */}
         {investmentPieData.length > 0 && (
            <div className={`w-full pt-5 border-t flex flex-col items-center ${isDark ? "border-slate-700/40" : "border-slate-200"}`}>
               <p className={`text-xs font-extrabold uppercase tracking-wider mb-3 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                  Investment Breakdown
               </p>
               <ResponsiveContainer width="100%" height={200}>
                  <PieChart>
                     <Pie
                        data={investmentPieData}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        outerRadius={75}
                        innerRadius={30}
                        paddingAngle={2}
                        dataKey="value"
                     >
                        {investmentPieData.map((entry, index) => (
                           <Cell
                              key={`cell-${index}`}
                              fill={activeColors[index % activeColors.length]}
                              stroke={isDark ? "#1e293b" : "#ffffff"}
                              strokeWidth={1.5}
                           />
                        ))}
                     </Pie>
                     <Tooltip
                        formatter={(value, name) => [`$${value.toFixed(2)}`, name]}
                        itemStyle={{ color: isDark ? '#f8fafc' : '#0f172a' }}
                        labelStyle={{ color: isDark ? '#cbd5e1' : '#475569', fontWeight: 'bold' }}
                        contentStyle={{
                           backgroundColor: isDark ? '#0f172a' : '#ffffff',
                           border: isDark ? '1px solid #334155' : '1px solid #e2e8f0',
                           borderRadius: '12px',
                           color: isDark ? '#f8fafc' : '#0f172a',
                           boxShadow: isDark ? '0 10px 25px -5px rgba(0, 0, 0, 0.6)' : '0 10px 15px -3px rgba(0,0,0,0.1)',
                           fontWeight: 700
                        }}
                     />
                  </PieChart>
               </ResponsiveContainer>

               {/* Legend */}
               <div className="flex flex-wrap justify-center gap-x-4 gap-y-2 mt-3 px-2 max-w-xl">
                  {investmentPieData.map((entry, index) => (
                     <div key={entry.name || index} className="flex items-center gap-1.5 text-xs">
                        <span
                           className="w-3 h-3 rounded-sm flex-shrink-0"
                           style={{ backgroundColor: activeColors[index % activeColors.length] }}
                        />
                        <span className={`font-semibold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                           {entry.name}
                        </span>
                        <span className={`font-bold ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                           ${entry.value.toFixed(0)}
                        </span>
                     </div>
                  ))}
               </div>
            </div>
         )}

         {/* View Investments Analysis Button */}
         {month && year && (
            <div className="flex justify-center mt-6 pt-4 border-t border-slate-700/30">
               <button
                  className={`group flex items-center gap-2.5 px-5 py-2.5 rounded-xl text-xs font-extrabold uppercase tracking-wider transition-all duration-300 shadow-md hover:shadow-xl hover:scale-105 active:scale-95 ${isDark
                     ? "bg-gradient-to-r from-teal-600 via-teal-500 to-emerald-600 hover:from-teal-500 hover:to-emerald-500 text-white border border-teal-400/30"
                     : "bg-gradient-to-r from-teal-600 via-teal-600 to-emerald-600 hover:from-teal-500 hover:to-emerald-500 text-white"
                     }`}
                  type="button"
                  onClick={() => navigate(`/investments/analysis/${month.toLowerCase()}/${year}`)}
               >
                  <FaChartLine size={13} className="transition-transform group-hover:scale-110" />
                  <span>View Investment Analysis</span>
                  <FaArrowRight size={11} className="transition-transform group-hover:translate-x-1" />
               </button>
            </div>
         )}
      </div>
   );
};

export default FinancialGrowthSummary;
