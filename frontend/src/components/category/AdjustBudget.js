import React from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";

const AdjustBudget = ({ categories, onSliderChange }) => {
  return (
    <div className="mb-8 max-h-64 overflow-y-auto">
      <h2 className="text-2xl font-bold mb-4 text-white">Adjust Budget</h2>
      {categories.map((category) => (
        <div key={category} className="mb-4">
          <label className="block mb-2 font-bold text-white">{category}</label>
          <div className="relative">
            <Slider
              defaultValue={0}
              min={0}
              max={1}
              step={0.01}
              onChange={(value) => onSliderChange(category, value)}
              handleClassName="slider-handle"
              trackClassName="bg-transparent"
              railClassName="bg-gray-500 h-2 rounded-full"
            />
          </div>
        </div>
      ))}
    </div>
  );
};

export default AdjustBudget;
