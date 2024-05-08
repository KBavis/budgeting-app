import React, { useState } from "react";

/**
 * Component to allow users the freedom to add their own Category
 *
 * @param onSubmit
 *       - function to handle the submission of our Category
 * @returns
 */
const UserInput = ({ onSubmit }) => {
   const [category, setCategory] = useState("");

   //Handles the submission of our Category
   const handleSubmit = (e) => {
      e.preventDefault();
      if (category.trim() !== "") {
         onSubmit(category);
         setCategory("");
      }
   };

   return (
      <div className="mt-16 mb-8 flex justify-center">
         <div className="mb-8 max-w-md w-full">
            <h2 className="text-lg font-semibold mb-4 text-white text-center">
               Add Your Own Category
            </h2>
            <form
               onSubmit={handleSubmit}
               className="flex items-center justify-center"
            >
               <input
                  type="text"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  placeholder="Enter a category"
                  className="border-[2px] border-indigo-600 rounded p-1 text-sm mr-4 h-10 w-60"
               />
               <button
                  type="submit"
                  className="flex items-center font-bold py-2 px-2 rounded text-white bg-indigo-600 hover:bg-indigo-700 transition-colors duration-300 text-[11px] border border-gray-300 h-10"
               >
                  Add
               </button>
            </form>
         </div>
      </div>
   );
};

export default UserInput;
