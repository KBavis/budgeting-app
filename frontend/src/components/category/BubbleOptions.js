import React from "react";

const BubbleOptions = ({ onSelect, categoryType }) => {
   let options = [];

   switch (categoryType) {
      case "Needs":
         options = ["Groceries", "Housing", "Transportation"];
         break;
      case "Wants":
         options = ["Entertainment", "Dining Out", "Shopping"];
         break;
      case "Investments":
         options = ["Stocks", "Real Estate", "Retirement"];
         break;
      default:
         options = [];
   }

   return (
      <div className="mb-8">
         <h2 className="text-2xl font-bold mb-4 text-white">
            Select from Options
         </h2>
         <div className="flex flex-wrap justify-center">
            {options.map((option) => (
               <button
                  key={option}
                  onClick={() => onSelect(option)}
                  className="rounded-full bg-indigo-500 text-white px-4 py-2 m-2 hover:bg-indigo-600 transition-colors duration-300"
               >
                  {option}
               </button>
            ))}
         </div>
      </div>
   );
};

export default BubbleOptions;
