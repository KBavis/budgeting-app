import React from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css"; // Import default styles for rc-slider

/**
 *
 * @param categoryType
 *          - The CategoryType this specififed slider is being used for
 * @param onSliderChange
 *          - function to handle the changing of allocations for our CategoryType
 * @returns
 */
const CategoryTypeSlider = ({ categoryType, onSliderChange }) => {
   //Handling of the slider change
   const handleSliderChange = (value) => {
      const clampedValue = Math.max(0.01, Math.min(0.99, value));
      onSliderChange(categoryType.name, clampedValue);
   };

   return (
      <div className="w-full mb-8">
         <label className="block mb-2 font-bold text-white">
            {categoryType.name}
         </label>
         <div className="relative">
            <Slider
               value={categoryType.budgetAllocationPercentage}
               onChange={handleSliderChange}
               min={0}
               max={1}
               step={0.01}
               handleClassName="slider-handle"
               trackClassName="bg-transparent"
               railClassName="bg-gray-500 h-2 rounded-full"
            />
         </div>
         <div className="mt-2 text-center text-gray-400">
            {(categoryType.budgetAllocationPercentage * 100).toFixed(0)}%
         </div>
      </div>
   );
};

export default CategoryTypeSlider;
