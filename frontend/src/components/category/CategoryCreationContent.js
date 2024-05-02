import React, { useState, useContext } from "react";
import BubbleOptions from "./BubbleOptions";
import UserInput from "./UserInput";
import AdjustBudget from "./AdjustBudget";
import categoryContext from "../context/category/categoryContext";

const CategoryCreationContent = ({ categoryType }) => {
  const [selectedCategories, setSelectedCategories] = useState([]);
  const { addCategories } = useContext(categoryContext);

  const handleOptionSelect = (category) => {
    setSelectedCategories([...selectedCategories, category]);
  };

  const handleUserInput = (category) => {
    setSelectedCategories([...selectedCategories, category]);
  };

  const handleSubmit = () => {
    const categoriesWithType = selectedCategories.map((category) => ({
      ...category,
      //categoryTypeId: getCategoryTypeId(categoryType),
      //TODO: Set cateogoryTypeId from Context
    }));
    addCategories(categoriesWithType);
  };

  return (
    <div>
      <BubbleOptions onSelect={handleOptionSelect} />
      <UserInput onSubmit={handleUserInput} />
      <AdjustBudget
        categories={selectedCategories}
        onSliderChange={(name, value) => console.log(`${name}: ${value}`)}
      />
      <button
        onClick={handleSubmit}
        className="font-bold py-3 px-6 rounded text-white bg-indigo-600 hover:bg-indigo-700 transition-colors duration-300"
      >
        Submit
      </button>
    </div>
  );
};

export default CategoryCreationContent;
