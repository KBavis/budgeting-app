import React from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";

/**
 * TODO:
 *  a) ensure that the slider isn't cut off all the way to the left and all the way to the right
 *  b) remove the originally scroll wheel and add custom scroll wheel
 *  c) change styling so the background is not all white
 */
const AdjustBudget = ({ categories, onSliderChange, totalBudget }) => {
   return (
      <div className="mb-8 p-6 bg-white rounded-lg shadow-md">
         <h2 className="text-2xl font-bold mb-4 text-indigo-700">
            Adjust Budget
         </h2>
         <p className="mb-4 text-base italic">
            Total Amount Available to Allocate:{" "}
            <span className="font-bold">${totalBudget.toFixed(2)}</span>
         </p>
         <div className="max-h-[300px] overflow-y-auto">
            {categories.map((category) => (
               <div key={category.name} className="mb-6">
                  <label className="block mb-2 font-bold">
                     {category.name}
                  </label>
                  <div className="relative mb-2 py-4">
                     <Slider
                        value={category.budgetAllocationPercentage}
                        min={0}
                        max={1}
                        step={0.01}
                        onChange={(value) =>
                           onSliderChange(category.name, value, totalBudget)
                        }
                        handleClassName="slider-handle"
                        trackClassName="bg-transparent"
                        railClassName="bg-gray-300 h-2 rounded-full"
                        handleStyle={{
                           borderColor: "rgb(79, 70, 229)",
                           height: 20,
                           width: 20,
                           marginTop: -8,
                           backgroundColor: "rgb(99, 102, 241)",
                        }}
                        railStyle={{ height: 6 }}
                        trackStyle={{ height: 6 }}
                     />
                  </div>
                  <div className="flex justify-between">
                     <div className="text-gray-600">
                        {(category.budgetAllocationPercentage * 100).toFixed(0)}
                        %
                     </div>
                     <div className="text-gray-600">
                        ${category.budgetAmount.toFixed(2)}
                     </div>
                  </div>
               </div>
            ))}
         </div>
      </div>
   );
};

export default AdjustBudget;
