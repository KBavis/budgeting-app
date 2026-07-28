import React, { useContext, useState } from 'react';
import { ThemeContext } from '../../context/theme/ThemeContext';
import './styles.css';

const CategorySlider = ({ categoryType, categories, onCategorySelect, onCancel }) => {
  const [animation, setAnimation] = useState('slide-up');
  const { theme } = useContext(ThemeContext);
  const isDark = theme === "dark";

  const handleSelect = (category) => {
    setAnimation('slide-down');
    setTimeout(() => onCategorySelect(category), 300); // Wait for animation
  };

  const handleCancel = () => {
    setAnimation('slide-down');
    setTimeout(onCancel, 300); // Wait for animation
  };

  return (
    <div
      className={`category-slider ${animation} ${
        isDark
          ? "bg-slate-900 border-t border-slate-700 text-slate-100 shadow-2xl"
          : "bg-white border-t border-slate-200 text-slate-900 shadow-2xl"
      }`}
    >
      <div className="slider-content max-w-md mx-auto p-6 text-center">
        <h2 className={`text-base font-bold uppercase tracking-wider mb-4 ${isDark ? "text-indigo-400" : "text-brand-600"}`}>
          {categoryType.name}
        </h2>
        <div className="category-buttons flex flex-col gap-2.5 max-h-[50vh] overflow-y-auto pr-1">
          {categories.map((category) => (
            <button
              key={category.categoryId}
              onClick={() => handleSelect(category)}
              className={`w-full py-3.5 px-4 rounded-xl text-sm font-bold transition-all text-center ${
                isDark
                  ? "bg-slate-800 border border-slate-700 text-slate-100 hover:bg-brand-600 hover:border-brand-500 hover:text-white"
                  : "bg-slate-50 border-2 border-slate-300 text-slate-900 hover:bg-brand-600 hover:border-brand-600 hover:text-white shadow-sm"
              }`}
            >
              {category.name}
            </button>
          ))}
        </div>
        <button
          onClick={handleCancel}
          className={`mt-4 w-full py-3 rounded-xl text-sm font-bold transition-all border ${
            isDark
              ? "bg-red-500/20 text-red-400 border-red-500/30 hover:bg-red-500 hover:text-white"
              : "bg-red-50 text-red-600 border-red-200 hover:bg-red-600 hover:text-white shadow-sm"
          }`}
        >
          Cancel
        </button>
      </div>
    </div>
  );
};

export default CategorySlider;