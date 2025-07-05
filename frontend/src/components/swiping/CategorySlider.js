import React, { useState } from 'react';
import './styles.css';

const CategorySlider = ({ categoryType, categories, onCategorySelect, onCancel }) => {
  const [animation, setAnimation] = useState('slide-up');

  const handleSelect = (category) => {
    setAnimation('slide-down');
    setTimeout(() => onCategorySelect(category), 300); // Wait for animation
  };

  const handleCancel = () => {
    setAnimation('slide-down');
    setTimeout(onCancel, 300); // Wait for animation
  };

  return (
    <div className={`category-slider ${animation}`}>
      <div className="slider-content">
        <h2>{categoryType.name}</h2>
        <div className="category-buttons">
          {categories.map((category) => (
            <button key={category.categoryId} onClick={() => handleSelect(category)}>
              {category.name}
            </button>
          ))}
        </div>
        <button onClick={handleCancel} className="cancel-button">
          Cancel
        </button>
      </div>
    </div>
  );
};

export default CategorySlider;