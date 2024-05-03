import React, { useRef, useState, useEffect } from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";
import { FaChevronUp, FaChevronDown, FaTimes } from "react-icons/fa";

const AdjustBudget = ({
   categories,
   onSliderChange,
   onRemoveCategory,
   totalBudget,
   remainingBudget,
}) => {
   const containerRef = useRef(null);
   const [showArrows, setShowArrows] = useState(false);

   useEffect(() => {
      const container = containerRef.current;
      if (container) {
         const hasOverflow = container.scrollHeight > container.clientHeight;
         setShowArrows(hasOverflow);
      }
   }, [categories]);

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

   return (
      <div className="mb-8 p-6 bg-gradient-to-br from-indigo-100 to-indigo-200 rounded-lg shadow-md relative">
         <h2 className="text-3xl font-bold mb-2 text-indigo-700">
            Adjust Budget
         </h2>
         <p className="mb-6 text-lg text-indigo-600">
            Total Amount Available to Allocate:{" "}
            <span className="font-bold text-indigo-800">
               ${remainingBudget.toFixed(2)}
            </span>
         </p>
         <div
            ref={containerRef}
            className="max-h-[300px] overflow-y-auto scrollbar-hide"
         >
            {categories.map((category) => (
               <div key={category.name} className="mb-6">
                  <div className="flex justify-center items-center mb-2">
                     <label className="block font-bold mr-2">
                        {category.name}
                     </label>
                     <button
                        className="text-red-500 hover:text-red-700 focus:outline-none"
                        onClick={() => onRemoveCategory(category.name)}
                     >
                        <div className="w-6 h-6 flex items-center justify-center bg-white rounded-full border-2 border-red-500">
                           <FaTimes size={14} />
                        </div>
                     </button>
                  </div>
                  <div className="relative mb-2 py-4 flex justify-between items-center">
                     <div className="text-gray-600 mr-4">
                        {(category.budgetAllocationPercentage * 100).toFixed(0)}
                        %
                     </div>
                     <Slider
                        className="w-3/4"
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
