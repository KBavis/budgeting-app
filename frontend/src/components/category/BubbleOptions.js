import React from "react";
import { FaPlus } from "react-icons/fa";

/**
 * BubbleOptions — Quick option buttons to select common categories for a category bucket
 */
const BubbleOptions = ({ onSelect, categoryType, selectedCategories }) => {
   let options = [];

   switch (categoryType) {
      case "Needs":
         options = [
            "Groceries",
            "Rent & Housing",
            "Utilities & Bills",
            "Transportation",
            "Student Loans",
            "Medical & Health",
         ];
         break;
      case "Wants":
         options = [
            "Dining Out",
            "Entertainment",
            "Shopping",
            "Travel & Vacation",
            "Hobbies",
            "Subscriptions",
         ];
         break;
      case "Investments":
         options = [
            "Emergency Fund",
            "Stock Market / Index Funds",
            "Retirement (401k/IRA)",
            "Real Estate",
            "Crypto",
         ];
         break;
      default:
         options = [];
   }

   const filteredOptions = options.filter(
      (option) =>
         !selectedCategories.some(
            (cat) => cat.name.toLowerCase() === option.toLowerCase()
         )
   );

   if (filteredOptions.length === 0) return null;

   return (
      <div className="mb-6 text-left">
         <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2.5">
            Suggested Categories:
         </p>
         <div className="flex flex-wrap gap-2">
            {filteredOptions.map((option) => (
               <button
                  key={option}
                  type="button"
                  onClick={() => onSelect(option)}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-brand-600 text-slate-200 hover:text-white border border-slate-700/80 hover:border-brand-500 text-xs font-semibold transition-all duration-200 shadow-sm"
               >
                  <FaPlus className="w-2.5 h-2.5 text-slate-400 hover:text-white" />
                  <span>{option}</span>
               </button>
            ))}
         </div>
      </div>
   );
};

export default BubbleOptions;
