import React, { useRef, useState, useEffect } from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import { FaChevronUp, FaChevronDown, FaTimes } from "react-icons/fa";

/**
 * Component utilized to allow users to adjust their budget as they see fit
 *
 * @param categories
 *          -  selected Categories to display in the AdjustBudget component
 * @param onSliderChange
 *          - on slider change function to correctly adjust amounts allocated and amount remaining of budget
 * @param onRemoveCategory
 *          - on remove function to handle when a user de-selects a selected cateogory
 * @param totalBudget
 *          - total budget user has to work with (total sum of incomes)A
 * @param remainingBudget
 *          - total budget remaining based on allocated amount to each category
 *
 */
const AdjustBudget = ({
   categories,
   onSliderChange,
   onRemoveCategory,
   totalBudget,
   remainingBudget,
}) => {
   //Local State
   const containerRef = useRef(null);
   const [showArrows, setShowArrows] = useState(false);

   //Use Effect for displaying arrows for user to scroll within component
   useEffect(() => {
      const container = containerRef.current;
      if (container) {
         const hasOverflow = container.scrollHeight > container.clientHeight;
         setShowArrows(hasOverflow);
      }
   }, [categories]);

   //Customize scroll functionality based on arrows
   const handleScroll = (direction) => {
      if (containerRef.current) {
         const container = containerRef.current;
         const scrollAmount = direction === "up" ? -200 : 200;
         container.scrollBy({
            top: scrollAmount,
            behavior: "smooth",
         });
      }
   };

   //Function to get appropaite font color based on amount of budget remaining
   const getAmountColor = () => {
      if (remainingBudget < 0) {
         return "text-red-500";
      } else if (remainingBudget < 500) {
         return "text-yellow-500";
      } else {
         return "text-green-500";
      }
   };

   return (
      <div className="mb-8 mx-5 p-6 bg-white border-[3px] border-indigo-600 rounded-lg shadow-md relative xs:p-4">
         <h2 className="text-3xl font-bold mb-4 text-indigo-800 xs:text-2xl">
            Adjust Budget
         </h2>
         <div className="p-4 bg-gray-100 rounded-md shadow-md mb-6 xs:p-2">
            <p className="text-lg font-semibold text-gray-700 xs:text-base">
               Total Amount Available:{" "}
               <span className={`font-bold ${getAmountColor()} text-xl xs:text-lg`}>
                  ${remainingBudget.toFixed(2)}
               </span>
            </p>
            <p className="mt-4 xs:text-sm">
               In case you use less than your allocated budget, the remaining
               amount will be applied to your savings.
            </p>
         </div>
         <div
            ref={containerRef}
            className="max-h-[300px] overflow-y-auto scrollbar-hide"
         >
            {categories.map((category) => (
               <div key={category.name} className="mb-6">
                  <div className="flex justify-center items-center mt-3 mb-[2px]">
                     <label className="block font-bold mr-2">
                        {category.name}
                     </label>
                     <button
                        className="text-red-500 hover:text-gray-700 focus:outline-none"
                        onClick={() => onRemoveCategory(category.name)}
                     >
                        <FaTimes size={14} />
                     </button>
                  </div>
                  <div className="relative mb-2 py-4 flex justify-between items-center">
                     <div className="text-gray-600 mr-4">
                        {(category.budgetAllocationPercentage * 100).toFixed(0)}
                        %
                     </div>
                     <Slider
                        className="w-full sm:w-3/4"
                        value={category.budgetAllocationPercentage}
                        min={0}
                        max={1}
                        step={0.01}
                        onChange={(value) =>
                           onSliderChange(category.name, value, totalBudget)
                        }
                        handleStyle={{
                           borderColor: "rgb(79, 70, 229)",
                           height: 20,
                           width: 20,
                           marginTop: -8,
                           backgroundColor: "rgb(99, 102, 241)",
                        }}
                        railStyle={{
                           backgroundColor: "rgb(156, 163, 175)",
                           height: 6,
                        }}
                        trackStyle={{
                           backgroundColor: "rgb(79, 70, 229)",
                           height: 6,
                        }}
                        dotStyle={{ display: "none" }}
                        activeDotStyle={{ display: "none" }}
                     />
                     <div className="text-gray-600 ml-4">
                        $
                        {(
                           category.budgetAllocationPercentage * totalBudget
                        ).toFixed(2)}
                     </div>
                  </div>
               </div>
            ))}
         </div>
         {showArrows && (
            <div className="absolute top-0 right-4 h-full flex flex-col justify-between items-center py-4">
               <button
                  className="p-2 bg-indigo-500 text-white rounded-full shadow-md hover:bg-indigo-600 focus:outline-none"
                  onClick={() => handleScroll("up")}
               >
                  <FaChevronUp />
               </button>
               <button
                  className="p-2 bg-indigo-500 text-white rounded-full shadow-md hover:bg-indigo-600 focus:outline-none"
                  onClick={() => handleScroll("down")}
               >
                  <FaChevronDown />
               </button>
            </div>
         )}
      </div>
   );
};

export default AdjustBudget;
