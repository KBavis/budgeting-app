import React, { useContext, useEffect, useState } from "react";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";
import { useNavigate } from "react-router-dom";
import { FaChartLine, FaArrowRight } from "react-icons/fa";
import CategoryPerformanceContext from "../../context/category/performances/categoryPerformanceContext";
import categoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import { ThemeContext } from "../../context/theme/ThemeContext";
import { getBudgetStatus } from "../../utils/budgetColors";

/**
 * Component used to represent a BudgetOverview in our BudgetPerformance entity
 * with full Light/Dark mode support.
 */
const BudgetOverview = ({ overview, month, year }) => {
   const navigate = useNavigate();
   const [pieData, setPieData] = useState([]);
   const [categoryMap, setCategoryMap] = useState({});
   const [currentTypeId, setCurrentTypeId] = useState(null);

   const { category_performances } = useContext(CategoryPerformanceContext);
   const { categories, fetchCategories } = useContext(categoryContext);
   const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext);
   const { theme } = useContext(ThemeContext);

   const isDark = theme === "dark";

   const {
      overviewType = "GENERAL",
      totalSpent = 0,
      totalAmountAllocated = 0,
      totalPercentUtilized = 0,
      totalAmountSaved = 0,
      savedAmountAttributesTotal = 0,
   } = overview || {};

   const budgetStatus = getBudgetStatus(totalSpent, totalAmountAllocated);

   const getCategoryName = (id) => {
      return categoryMap[id] || "Unknown";
   };

   const convertToNormalCase = (str) => {
      if (!str) return "";
      return str
         .toLowerCase()
         .split(" ")
         .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
         .join(" ");
   };

   // fetch category types if page is refreshed
   useEffect(() => {
      if (!categoryTypes || categoryTypes.length === 0) {
         fetchCategoryTypes();
      }
   }, [categoryTypes]);

   // set category type ID
   useEffect(() => {
      if (!categoryTypes || !overviewType) return;

      let currCategoryTypename = convertToNormalCase(overviewType);
      let type = categoryTypes.find((t) => t.name === currCategoryTypename);

      if (type) {
         setCurrentTypeId(type.categoryTypeId);
      }
   }, [categoryTypes, overviewType]);

   // generate category mapping
   useEffect(() => {
      if (!categories || categories.length === 0) return;

      const mapping = {};
      categories.forEach((cat) => {
         mapping[cat.categoryId] = cat.name;
      });
      setCategoryMap(mapping);
   }, [categories]);

   // fetch user categories if page is refreshed
   useEffect(() => {
      if (!categories || categories.length === 0) {
         fetchCategories();
      }
   }, [categories]);

   // generate relevant pie chart data when category performances refreshed
   useEffect(() => {
      if (!category_performances || category_performances.length === 0 || currentTypeId == null) {
         return;
      }

      const filteredPerformances = category_performances
         .filter((curr) => !curr.categoryTypeId || String(curr.categoryTypeId) === String(currentTypeId))
         .filter((curr) => (curr.totalSpend || 0) > 0);

      const currPieData = filteredPerformances.map((performance) => ({
         name: getCategoryName(performance.categoryId),
         value: parseFloat((performance.totalSpend || 0).toFixed(2))
      }));
      setPieData(currPieData);
   }, [category_performances, currentTypeId, categoryMap]);

   if (!overview) return null;

   // Carefully selected distinct colors for Dark Mode vs Light Mode
   const DARK_COLORS = [
      "#818cf8", // Indigo
      "#f472b6", // Pink
      "#34d399", // Emerald
      "#fbbf24", // Amber
      "#38bdf8", // Sky Blue
      "#a78bfa", // Purple
      "#fb7185", // Rose
      "#4ade80", // Green
      "#f97316", // Orange
      "#2dd4bf", // Teal
   ];

   const LIGHT_COLORS = [
      "#4f46e5", // Deep Indigo
      "#ec4899", // Pink
      "#059669", // Emerald
      "#d97706", // Amber
      "#0284c7", // Sky Blue
      "#7c3aed", // Deep Purple
      "#e11d48", // Rose
      "#16a34a", // Green
      "#ea580c", // Orange
      "#0d9488", // Teal
   ];

   const activeColors = isDark ? DARK_COLORS : LIGHT_COLORS;

   const getTextColor = (value) => {
      return value >= 0 ? (isDark ? "text-emerald-400" : "text-emerald-600") : (isDark ? "text-red-400" : "text-red-600");
   };

   const getOverUnderText = (value) => {
      return value >= 0 ? "Under" : "Over";
   };

   const pctDisplay = (totalPercentUtilized * 100).toFixed(1);
   const barWidth = Math.min(totalPercentUtilized * 100, 100);

   const pieHeight = pieData.length > 6 ? 280 : (pieData.length > 3 ? 240 : 210);

   return (
      <div className={`relative rounded-2xl shadow-lg p-6 mx-auto w-full max-w-4xl mb-8 border transition-all ${
         isDark
            ? "bg-slate-900/80 border-slate-700/60 text-slate-100"
            : "bg-white border-slate-200 text-slate-800"
      }`}>
         {/* Title */}
         <h3 className={`text-xl font-bold mb-4 text-center ${isDark ? "text-white" : "text-slate-900"}`}>
            {convertToNormalCase(overviewType)} Overview
         </h3>

         {/* Spent / Allocated Summary */}
         <div className={`text-center mb-3 text-base font-semibold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
            Spent{" "}
            <span className={`font-extrabold ${budgetStatus.textClass}`}>
               ${totalSpent.toFixed(2)}
            </span>{" "}
            out of{" "}
            <span className={`font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
               ${totalAmountAllocated.toFixed(2)}
            </span>
         </div>

         {/* Progress Bar */}
         <div className={`w-full rounded-full h-3 mb-4 ${isDark ? "bg-slate-800" : "bg-slate-200"}`}>
            <div
               className={`h-3 rounded-full transition-all duration-500 ease-in-out ${budgetStatus.colorClass}`}
               style={{ width: `${barWidth}%` }}
            ></div>
         </div>

         {/* Stats Grid */}
         <div className="grid grid-cols-3 gap-4 text-center mb-4">
            <div>
               <p className={`text-xs font-bold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                  Utilization
               </p>
               <p className={`text-lg font-bold ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                  {pctDisplay}%
               </p>
            </div>
            <div>
               <p className={`text-xs font-bold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                  {getOverUnderText(savedAmountAttributesTotal)} Budget
               </p>
               <p className={`text-lg font-bold ${getTextColor(savedAmountAttributesTotal)}`}>
                  ${Math.abs(savedAmountAttributesTotal).toFixed(2)}
               </p>
            </div>
            <div>
               <p className={`text-xs font-bold uppercase tracking-wider mb-0.5 text-teal-600 dark:text-teal-400`}>
                  Net Wealth Built
               </p>
               <p className={`text-lg font-bold text-teal-600 dark:text-teal-400`}>
                  ${(totalAmountSaved > 0 ? totalAmountSaved : Math.max(0, savedAmountAttributesTotal)).toFixed(2)}
               </p>
            </div>
         </div>

         {/* Pie Chart Section */}
         {pieData.length > 0 && (
            <div className={`w-full mt-6 pt-5 border-t flex flex-col items-center ${isDark ? "border-slate-700/40" : "border-slate-200"}`}>
               <ResponsiveContainer width="100%" height={200}>
                  <PieChart>
                     <Pie
                        data={pieData}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        outerRadius={75}
                        innerRadius={0}
                        paddingAngle={1}
                        dataKey="value"
                     >
                        {pieData.map((entry, index) => (
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

               {/* Custom Responsive HTML Legend */}
               <div className="flex flex-wrap justify-center gap-x-4 gap-y-2 mt-3 px-2 max-w-xl">
                  {pieData.map((entry, index) => (
                     <div key={entry.name || index} className="flex items-center gap-1.5 text-xs">
                        <span
                           className="w-3 h-3 rounded-sm flex-shrink-0"
                           style={{ backgroundColor: activeColors[index % activeColors.length] }}
                        />
                        <span className={`font-semibold text-xs ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                           {entry.name}
                        </span>
                     </div>
                  ))}
               </div>
            </div>
         )}

         {/* Spending Analysis Action Button */}
         {overviewType !== "GENERAL" && month && year && (
            <div className="flex justify-center mt-6 pt-4 border-t border-slate-700/30">
               <button
                  className={`group flex items-center gap-2.5 px-5 py-2.5 rounded-xl text-xs font-extrabold uppercase tracking-wider transition-all duration-300 shadow-md hover:shadow-xl hover:scale-105 active:scale-95 ${
                     isDark
                        ? "bg-gradient-to-r from-indigo-600 via-indigo-500 to-violet-600 hover:from-indigo-500 hover:to-violet-500 text-white border border-indigo-400/30"
                        : "bg-gradient-to-r from-indigo-600 via-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500 text-white"
                  }`}
                  type="button"
                  onClick={() => navigate(`/${overviewType.toLowerCase()}/analysis/${month.toLowerCase()}/${year}`)}
               >
                  <FaChartLine size={13} className="transition-transform group-hover:scale-110" />
                  <span>View {convertToNormalCase(overviewType)} Analysis</span>
                  <FaArrowRight size={11} className="transition-transform group-hover:translate-x-1" />
               </button>
            </div>
         )}
      </div>
   );
};

export default BudgetOverview;
