import React from "react";
import { CSSTransition, TransitionGroup } from "react-transition-group";

const BubbleOptions = ({ onSelect, categoryType, selectedCategories }) => {
   let options = [];

   switch (categoryType) {
      case "Needs":
         options = [
            "Groceries",
            "Rent",
            "Transportation",
            "Student Loans",
            "Animals",
         ];
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

   const filteredOptions = options.filter(
      (option) =>
         !selectedCategories.some(
            (cat) => cat.name.toLowerCase() === option.toLowerCase()
         )
   );

   return (
      <div className="mt-16 mb-8">
         <h2 className="text-xl font-semibold mb-2 text-white tracking-wide">
            Select from Options
         </h2>
         <TransitionGroup
            component="div"
            className="flex flex-wrap justify-center"
         >
            {filteredOptions.map((option) => (
               <CSSTransition key={option} timeout={300} classNames="fade">
                  <button
                     onClick={() => onSelect(option)}
                     className="rounded-full bg-indigo-600 text-white font-semibold border-2 border-indigo-600 text-xs px-2 py-2 m-2 shadow-md hover:bg-transparent transition-colors duration-300"
                  >
                     {option}
                  </button>
               </CSSTransition>
            ))}
         </TransitionGroup>
      </div>
   );
};

export default BubbleOptions;
