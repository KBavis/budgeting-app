import React, { useState, useContext } from "react";
import BubbleOptions from "./BubbleOptions";
import UserInput from "./UserInput";
import AdjustBudget from "./AdjustBudget";
import categoryContext from "../../context/category/categoryContext";
import IncomeContext from "../../context/income/incomeContext";
import categoryTypeContext from "../../context/categoryTypes/categoryTypeContext";

const CategoryCreationContent = ({ categoryType }) => {
   const [selectedCategories, setSelectedCategories] = useState([]);
   const { addCategories } = useContext(categoryContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const { incomes } = useContext(IncomeContext);

   const handleOptionSelect = (category) => {
      setSelectedCategories([
         ...selectedCategories,
         { name: category, budgetAllocationPercentage: 0, budgetAmount: 0 },
      ]);
   };

   const handleUserInput = (category) => {
      setSelectedCategories([
         ...selectedCategories,
         { name: category, budgetAllocationPercentage: 0, budgetAmount: 0 },
      ]);
   };

   const handleSliderChange = (name, value, totalBudget) => {
      const updatedCategories = selectedCategories.map((cat) =>
         cat.name === name
            ? {
                 ...cat,
                 budgetAllocationPercentage: value,
                 budgetAmount: value * totalBudget,
              }
            : cat
      );

      setSelectedCategories(updatedCategories);
   };

   const handleSubmit = () => {
      const categoriesWithType = selectedCategories.map((category) => ({
         ...category,
         categoryTypeId: getCategoryTypeId(categoryType),
      }));
      addCategories(categoriesWithType);
   };

   const getCategoryTypeId = (categoryTypeName) => {
      const categoryType = categoryTypes.find(
         (type) => type.name === categoryTypeName
      );
      return categoryType ? categoryType.id : null;
   };

   const getTotalIncome = () => {
      return incomes.reduce((total, income) => total + income.amount, 0);
   };

   const getBudgetAllocationPercentage = (categoryTypeName) => {
      const categoryType = categoryTypes.find(
         (type) => type.name === categoryTypeName
      );
      return categoryType ? categoryType.budgetAllocationPercentage : 0;
   };

   const getTotalBudget = () => {
      const totalIncome = getTotalIncome();
      const budgetAllocationPercentage =
         getBudgetAllocationPercentage(categoryType);
      return totalIncome * budgetAllocationPercentage;
   };

   return (
      <div>
         <BubbleOptions
            onSelect={handleOptionSelect}
            categoryType={categoryType}
         />
         <UserInput onSubmit={handleUserInput} />
         <AdjustBudget
            categories={selectedCategories}
            onSliderChange={handleSliderChange}
            totalBudget={getTotalBudget()}
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
