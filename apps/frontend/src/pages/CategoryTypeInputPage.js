import React, { useState, useContext, useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import AlertContext from "../context/alert/alertContext";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryTypeSlider from "../components/category/types/CategoryTypeSlider";
import authContext from "../context/auth/authContext";
import IncomeContext from "../context/income/incomeContext";
import accountContext from "../context/account/accountContext";
import { FaInfoCircle, FaChartPie } from "react-icons/fa";

import StepProgress from "../components/layout/StepProgress";

/**
 * Page for adjusting allocated budget percentages for Needs, Wants, and Investments
 */
const CategoryTypeInputPage = () => {
   // Local State
   const [categoryTypesInput, setCategoryTypesInput] = useState([
      { name: "Needs", budgetAllocationPercentage: 0.5 },
      { name: "Wants", budgetAllocationPercentage: 0.3 },
      { name: "Investments", budgetAllocationPercentage: 0.2 },
   ]);

   // Global State
   const { setAlert } = useContext(AlertContext);
   const { error, addCategoryTypes, clearErrors } = useContext(categoryTypeContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { incomes, fetchIncomes } = useContext(IncomeContext);
   const { accounts, fetchAccounts } = useContext(accountContext);

   const navigate = useNavigate();

   // Calculate total monthly income sum
   const totalMonthlyIncome = useMemo(() => {
      if (!incomes || incomes.length === 0) return 0;
      return incomes.reduce((sum, inc) => sum + (parseFloat(inc.amount) || 0), 0);
   }, [incomes]);

   // Alert user of failure
   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error, clearErrors, setAlert]);

   // Fetch Needed Information on Refresh
   useEffect(() => {
      if (!user && localStorage.token) {
         fetchAuthenticatedUser();
      }

      if (!accounts || accounts.length === 0) {
         fetchAccounts();
      }

      if (!incomes || incomes.length === 0) {
         fetchIncomes();
      }
   }, []);

   // Function to handle user updating their budget allocation
   const handleSliderChange = (name, value) => {
      const updatedCategoryTypes = categoryTypesInput.map((cat) =>
         cat.name === name ? { ...cat, budgetAllocationPercentage: value } : cat
      );

      setCategoryTypesInput(updatedCategoryTypes);
   };

   // Function to handle user submitting their adjusted allocations
   const handleSubmit = () => {
      const totalPercentage = categoryTypesInput.reduce(
         (sum, cat) => sum + cat.budgetAllocationPercentage,
         0
      );

      const roundedTotal = Math.round(totalPercentage * 100) / 100;

      if (roundedTotal === 1.0) {
         addCategoryTypes(categoryTypesInput);
         setAlert("Category allocations configured successfully!", "success");
         navigate("/category/needs");
      } else {
         setAlert(
            `Total percentage must equal 100% (currently ${(roundedTotal * 100).toFixed(0)}%)`,
            "danger"
         );
      }
   };

   const totalPercent = useMemo(() => {
      const sum = categoryTypesInput.reduce(
         (acc, cat) => acc + cat.budgetAllocationPercentage,
         0
      );
      return Math.round(sum * 100);
   }, [categoryTypesInput]);

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center px-4 py-8">
         {/* Step Progress */}
         <StepProgress currentStep={4} totalSteps={5} />

         {/* Main Card */}
         <div className="max-w-lg w-full bg-slate-900/80 backdrop-blur-md border border-slate-700/80 rounded-2xl shadow-2xl p-8 animate-slide-up xs:p-6">
            <div className="flex items-center gap-3 mb-4">
               <div className="p-3 bg-brand-500/20 text-brand-400 rounded-2xl border border-brand-500/30">
                  <FaChartPie className="w-6 h-6" />
               </div>
               <div>
                  <h1 className="text-2xl md:text-3xl font-extrabold text-white">
                     Category Allocations
                  </h1>
                  <p className="text-xs text-slate-400">
                     Divide your monthly income into spending targets
                  </p>
               </div>
            </div>

            {/* Explanatory Banner */}
            <div className="bg-slate-800/80 border border-slate-700/80 rounded-xl p-4 mb-6 text-left text-xs text-slate-300 flex flex-col gap-2 shadow-inner">
               <div className="flex items-center gap-2 font-semibold text-brand-300 text-sm">
                  <FaInfoCircle className="w-4 h-4 flex-shrink-0" />
                  <span>How Budget Allocations Work</span>
               </div>
               <p className="leading-relaxed text-slate-300">
                  These sliders determine how much of your total monthly income (
                  <strong className="text-white">
                     ${totalMonthlyIncome.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                  </strong>
                  ) is assigned to each category type.
               </p>
               <ul className="list-disc list-inside space-y-1 text-slate-400 pl-1">
                  <li>
                     <strong className="text-slate-200">Needs (e.g., 50%)</strong>: Rent, groceries, utilities, essentials.
                  </li>
                  <li>
                     <strong className="text-slate-200">Wants (e.g., 30%)</strong>: Dining out, hobbies, shopping.
                  </li>
                  <li>
                     <strong className="text-slate-200">Investments (e.g., 20%)</strong>: Savings, retirement, emergency funds.
                  </li>
               </ul>
               <p className="text-[10px] text-amber-300/90 bg-amber-500/10 px-2.5 py-1.5 rounded-lg border border-amber-500/20 mt-1">
                  💡 <strong>Note:</strong> Unspent funds count as <strong>Savings</strong> (overspending reduces savings).
               </p>
            </div>

            {/* Total percentage summary pill */}
            <div className="flex justify-between items-center bg-slate-800/90 px-4 py-2.5 rounded-xl border border-slate-700/80 mb-6">
               <span className="text-xs font-semibold text-slate-300">Total Allocation:</span>
               <span
                  className={`text-sm font-bold px-2.5 py-0.5 rounded-full border ${
                     totalPercent === 100
                        ? "bg-emerald-500/20 text-emerald-400 border-emerald-500/30"
                        : "bg-red-500/20 text-red-400 border-red-500/30"
                  }`}
               >
                  {totalPercent}% / 100%
               </span>
            </div>

            {/* Sliders */}
            <div className="flex flex-col gap-6 mb-8">
               {categoryTypesInput.map((categoryType) => (
                  <CategoryTypeSlider
                     key={categoryType.name}
                     categoryType={categoryType}
                     totalIncome={totalMonthlyIncome}
                     onSliderChange={handleSliderChange}
                  />
               ))}
            </div>

            {/* Submit Button */}
            <button
               onClick={handleSubmit}
               className="w-full bg-brand-600 hover:bg-brand-500 text-white font-bold py-3 px-6 rounded-xl transition-all duration-300 shadow-lg hover:shadow-brand-500/25 hover:scale-[1.02]"
            >
               Confirm Allocations & Continue
            </button>
         </div>
      </div>
   );
};

export default CategoryTypeInputPage;
