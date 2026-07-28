import React, { useState } from "react";
import { FaPlus } from "react-icons/fa";

/**
 * UserInput — Custom category input component
 */
const UserInput = ({ onSubmit }) => {
   const [category, setCategory] = useState("");

   const handleSubmit = (e) => {
      e.preventDefault();
      if (category.trim() !== "") {
         onSubmit(category.trim());
         setCategory("");
      }
   };

   return (
      <form onSubmit={handleSubmit} className="mb-6 flex gap-2">
         <input
            type="text"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            placeholder="Add custom category name..."
            className="flex-1 bg-slate-800 border border-slate-700 text-slate-100 px-3.5 py-2 rounded-xl text-xs focus:outline-none focus:border-brand-500 transition-colors placeholder-slate-500"
         />
         <button
            type="submit"
            disabled={!category.trim()}
            className="px-4 py-2 bg-brand-600 hover:bg-brand-500 disabled:opacity-40 text-white font-semibold rounded-xl text-xs transition-all duration-200 flex items-center gap-1.5 shadow-md"
         >
            <FaPlus className="w-3 h-3" />
            <span>Add</span>
         </button>
      </form>
   );
};

export default UserInput;
