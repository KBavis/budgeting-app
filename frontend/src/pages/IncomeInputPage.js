import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AlertContext from "../context/alert/alertContext";
import IncomeContext from "../context/income/incomeContext";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";
import { FaWallet, FaPlus, FaTrashAlt, FaArrowRight, FaCheckCircle } from "react-icons/fa";

/**
 * Registration step progress bar — shows "Step 3 of 4"
 */
const StepProgress = ({ currentStep = 3, totalSteps = 4 }) => {
   const steps = ["Register", "Connect Accounts", "Income", "Allocations"];
   return (
      <div className="w-full max-w-xl mb-6 animate-fade-in">
         <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
               Step {currentStep} of {totalSteps}
            </span>
            <span className="text-xs font-semibold text-brand-300">
               {steps[currentStep - 1]}
            </span>
         </div>
         <div className="flex gap-1.5">
            {Array.from({ length: totalSteps }).map((_, i) => (
               <div
                  key={i}
                  className={`h-1.5 flex-1 rounded-full transition-all duration-500 ${
                     i < currentStep ? "bg-brand-500" : "bg-slate-700"
                  }`}
               />
            ))}
         </div>
      </div>
   );
};

/**
 * Page for users to enter single or multiple monthly income sources during registration
 */
const IncomeInputPage = () => {
   // Local Form States
   const [income, setIncome] = useState("");
   const [incomeSource, setIncomeSource] = useState("");
   const [incomeType, setIncomeType] = useState("");
   const [description, setDescription] = useState("");

   const navigate = useNavigate();
   
   // Global Contexts
   const { setAlert } = useContext(AlertContext);
   const { error, addIncome, removeIncome, fetchIncomes, clearErrors, incomes, totalIncome } = useContext(IncomeContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { accounts, fetchAccounts } = useContext(accountContext);

   // List of potential income sources
   const incomeSources = [
      "Employer",
      "Client",
      "Property",
      "Stock",
      "Mutual Fund",
      "Bond",
      "Savings Account",
      "Retirement Account",
      "Government",
      "Family",
      "Friend",
      "Business",
      "Intellectual Property",
      "Other",
   ];

   // List of potential income types
   const incomeTypes = [
      "Salary",
      "Bonus",
      "Commission",
      "Freelance",
      "Rental",
      "Investment",
      "Pension",
      "Social Security",
      "Alimony",
      "Child Support",
      "Royalties",
      "Capital Gains",
      "Gift",
      "Other",
   ];

   // Fetch user & incomes on mount
   useEffect(() => {
      if (!user && localStorage.token) {
         fetchAuthenticatedUser();
      }
      if (!accounts) {
         fetchAccounts();
      }
      fetchIncomes();
   }, []);

   // Handle error alerts
   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error, clearErrors, setAlert]);

   // Helper to clean enum display names
   const formatEnumLabel = (str) => {
      if (!str) return "";
      return str
         .toLowerCase()
         .split('_')
         .map(word => word.charAt(0).toUpperCase() + word.slice(1))
         .join(' ');
   };

   // Handle adding an income source
   const handleAddIncome = async (e) => {
      e.preventDefault();
      if (!income || isNaN(parseFloat(income)) || parseFloat(income) <= 0) {
         setAlert("Please enter a valid monthly income amount (> 0)", "danger");
         return;
      }

      const capitalizedSource = (incomeSource || "OTHER").toUpperCase().replace(/\s/g, "_");
      const capitalizedType = (incomeType || "SALARY").toUpperCase().replace(/\s/g, "_");
      
      const formData = {
         amount: parseFloat(income),
         incomeSource: capitalizedSource,
         incomeType: capitalizedType,
         description: description || `${formatEnumLabel(incomeSource || "OTHER")} Income`,
      };

      try {
         await addIncome(formData);
         setAlert("Income source added!", "SUCCESS");
         // Reset form input fields for adding multiple
         setIncome("");
         setDescription("");
         setIncomeSource("");
         setIncomeType("");
      } catch (err) {
         // Error handled by useEffect
      }
   };

   // Handle removing an income source
   const handleRemove = async (incomeId) => {
      try {
         await removeIncome(incomeId);
         setAlert("Income source removed", "SUCCESS");
      } catch (err) {
         // Error handled by useEffect
      }
   };

   // Proceed to next registration step
   const handleContinue = () => {
      if (!incomes || incomes.length === 0) {
         setAlert("Please add at least one income source before continuing.", "danger");
         return;
      }
      navigate("/category-types");
   };

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 justify-center items-center px-4 py-8">
         {/* Step Progress */}
         <StepProgress currentStep={3} totalSteps={4} />

         {/* Main Container */}
         <div className="max-w-xl w-full bg-slate-900/90 backdrop-blur-md border border-slate-700/80 rounded-2xl shadow-2xl p-6 sm:p-8 animate-slide-up text-left flex flex-col gap-6">
            
            {/* Header */}
            <div className="text-center flex flex-col items-center">
               <div className="inline-flex p-3 bg-brand-500/20 text-brand-400 rounded-2xl border border-brand-500/30 mb-3">
                  <FaWallet className="w-7 h-7" />
               </div>
               <h1 className="text-2xl sm:text-3xl font-extrabold text-white mb-1">
                  Monthly Income Sources
               </h1>
               <p className="text-xs sm:text-sm text-slate-400">
                  Add one or more income sources to calculate your total monthly budget targets.
               </p>
            </div>

            {/* List of Added Income Sources */}
            {incomes && incomes.length > 0 && (
               <div className="flex flex-col gap-3 p-4 bg-slate-800/60 border border-slate-700/60 rounded-xl">
                  <div className="flex items-center justify-between border-b border-slate-700/80 pb-2">
                     <span className="text-xs font-bold text-slate-300 uppercase tracking-wider flex items-center gap-1.5">
                        <FaCheckCircle className="text-emerald-400" />
                        Added Incomes ({incomes.length})
                     </span>
                     <span className="text-xs font-black text-brand-400">
                        Total: ${(totalIncome || 0).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}/mo
                     </span>
                  </div>

                  <div className="flex flex-col gap-2 max-h-48 overflow-y-auto pr-1">
                     {incomes.map((inc) => (
                        <div
                           key={inc.incomeId}
                           className="flex items-center justify-between p-3 bg-slate-900/90 border border-slate-700/80 rounded-xl text-sm transition-all hover:border-brand-500/50"
                        >
                           <div className="flex flex-col min-w-0 pr-2">
                              <span className="font-bold text-slate-100 truncate">
                                 {inc.description || formatEnumLabel(inc.incomeSource)}
                              </span>
                              <span className="text-[11px] text-slate-400">
                                 {formatEnumLabel(inc.incomeSource)} • {formatEnumLabel(inc.incomeType)}
                              </span>
                           </div>

                           <div className="flex items-center gap-3 flex-shrink-0">
                              <span className="font-extrabold text-emerald-400">
                                 +${parseFloat(inc.amount).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                              </span>
                              <button
                                 type="button"
                                 onClick={() => handleRemove(inc.incomeId)}
                                 className="p-1.5 text-slate-400 hover:text-red-400 hover:bg-red-500/10 rounded-lg transition-colors"
                                 title="Remove Income Source"
                              >
                                 <FaTrashAlt size={13} />
                              </button>
                           </div>
                        </div>
                     ))}
                  </div>
               </div>
            )}

            {/* Add New Income Form */}
            <form onSubmit={handleAddIncome} className="flex flex-col gap-4">
               <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-1">
                  Add Income Source
               </h3>

               <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {/* Amount */}
                  <div className="flex flex-col gap-1">
                     <label htmlFor="income" className="text-xs font-semibold text-slate-300">
                        Monthly Amount <span className="text-red-400">*</span>
                     </label>
                     <div className="relative flex items-center">
                        <span className="absolute left-3.5 text-slate-400 text-sm font-semibold">$</span>
                        <input
                           id="income"
                           type="number"
                           step="0.01"
                           value={income}
                           onChange={(e) => setIncome(e.target.value)}
                           className="w-full bg-slate-800 border border-slate-700 text-slate-100 pl-8 pr-3 py-2.5 rounded-xl text-sm focus:outline-none focus:border-brand-500 transition-colors placeholder-slate-500"
                           placeholder="0.00"
                           required
                        />
                     </div>
                  </div>

                  {/* Description */}
                  <div className="flex flex-col gap-1">
                     <label htmlFor="description" className="text-xs font-semibold text-slate-300">
                        Description
                     </label>
                     <input
                        id="description"
                        type="text"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        className="w-full bg-slate-800 border border-slate-700 text-slate-100 px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:border-brand-500 transition-colors placeholder-slate-500"
                        placeholder="e.g. Primary Job / Side Gig"
                     />
                  </div>
               </div>

               <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {/* Income Source */}
                  <div className="flex flex-col gap-1">
                     <label htmlFor="incomeSource" className="text-xs font-semibold text-slate-300">
                        Income Source
                     </label>
                     <select
                        id="incomeSource"
                        value={incomeSource}
                        onChange={(e) => setIncomeSource(e.target.value)}
                        className="w-full bg-slate-800 border border-slate-700 text-slate-100 px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:border-brand-500 transition-colors"
                     >
                        <option value="" disabled>Select Source</option>
                        {incomeSources.map((source) => (
                           <option key={source} value={source}>
                              {source}
                           </option>
                        ))}
                     </select>
                  </div>

                  {/* Income Type */}
                  <div className="flex flex-col gap-1">
                     <label htmlFor="incomeType" className="text-xs font-semibold text-slate-300">
                        Income Type
                     </label>
                     <select
                        id="incomeType"
                        value={incomeType}
                        onChange={(e) => setIncomeType(e.target.value)}
                        className="w-full bg-slate-800 border border-slate-700 text-slate-100 px-3 py-2.5 rounded-xl text-sm focus:outline-none focus:border-brand-500 transition-colors"
                     >
                        <option value="" disabled>Select Type</option>
                        {incomeTypes.map((type) => (
                           <option key={type} value={type}>
                              {type}
                           </option>
                        ))}
                     </select>
                  </div>
               </div>

               <button
                  type="submit"
                  className="w-full mt-1 bg-slate-800 hover:bg-slate-750 text-brand-300 border border-brand-500/40 hover:border-brand-500 font-bold py-2.5 px-4 rounded-xl transition-all duration-300 flex items-center justify-center gap-2 text-sm shadow-md"
               >
                  <FaPlus className="w-3.5 h-3.5" />
                  <span>Add Income Source</span>
               </button>
            </form>

            {/* Bottom Action: Continue to Step 4 */}
            <div className="pt-3 border-t border-slate-800 flex justify-end">
               <button
                  type="button"
                  onClick={handleContinue}
                  className="w-full bg-brand-600 hover:bg-brand-500 text-white font-bold py-3.5 px-6 rounded-xl transition-all duration-300 shadow-lg hover:shadow-brand-500/25 hover:scale-[1.01] flex items-center justify-center gap-2 text-sm"
               >
                  <span>Continue to Allocations</span>
                  <FaArrowRight className="w-3.5 h-3.5" />
               </button>
            </div>

         </div>
      </div>
   );
};

export default IncomeInputPage;
