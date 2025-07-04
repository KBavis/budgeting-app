import React from "react";
import { CSSTransition, TransitionGroup } from "react-transition-group";

/**
 *
 * @param onSelect
 *          - functionality to handle when a user selects one of our BubbleOptions
 * @param categoryType
 *          -  cateogory type used to determine which options to display
 * @param selectedCategories
 *          - selected categories utilized to filter out bubble options already selected
 * @returns
 */
const BubbleOptions = ({ onSelect, categoryType, selectedCategories }) => {
   let options = [];

   //Swithc case for determining list of options to use
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

   //Filters our potential bubble options based on selected categories
   const filteredOptions = options.filter(
      (option) =>
         !selectedCategories.some(
            (cat) => cat.name.toLowerCase() === option.toLowerCase()
         )
   );

   return (
      <div className="mt-16 mb-8">
         {filteredOptions.length > 0 && (
            <h2 className="text-xl font-semibold mb-2 text-white tracking-wide">
               Select from Options
            </h2>
         )}
         <TransitionGroup
            component="div"
            className="flex flex-wrap justify-center"
         >
            {filteredOptions.map((option) => (
               <CSSTransition key={option} timeout={300} classNames="fade">
                  <button
                     onClick={() => onSelect(option)}
                     className="rounded-full bg-indigo-600 text-white font-semibold border-2 border-indigo-600 text-xs px-2 py-2 m-2 shadow-md hover:bg-transparent transition-colors duration-300 xs:text-xxs xs:px-1 xs:py-1 xs:m-1"
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
