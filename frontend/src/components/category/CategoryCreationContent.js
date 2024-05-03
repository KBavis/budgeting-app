import React, { useState, useContext, useEffect } from "react";
import BubbleOptions from "./BubbleOptions";
import UserInput from "./UserInput";
import AdjustBudget from "./AdjustBudget";
import categoryContext from "../../context/category/categoryContext";
import IncomeContext from "../../context/income/incomeContext";
import categoryTypeContext from "../../context/categoryTypes/categoryTypeContext";
import AlertContext from "../../context/alert/alertContext";

const CategoryCreationContent = ({ categoryType }) => {
   const [selectedCategories, setSelectedCategories] = useState([]);
   const [remainingBudget, setRemainingBudget] = useState(0);
   const { addCategories } = useContext(categoryContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);
   const { incomes } = useContext(IncomeContext);

   useEffect(() => {
      const totalIncome = getTotalIncome();
      setRemainingBudget(totalIncome);
   }, [incomes]);

   const handleOptionSelect = (category) => {
      if (
         !selectedCategories.some(
            (cat) => cat.name.toLowerCase() === category.toLowerCase()
         )
      ) {
         setSelectedCategories([
            ...selectedCategories,
            { name: category, budgetAllocationPercentage: 0, budgetAmount: 0 },
         ]);
      } else {
         setAlert(`Category "${category}" has already been added.`, "danger");
      }
   };

   const handleUserInput = (category) => {
      if (
         !selectedCategories.some(
            (cat) => cat.name.toLowerCase() === category.toLowerCase()
         )
      ) {
         setSelectedCategories([
            ...selectedCategories,
            { name: category, budgetAllocationPercentage: 0, budgetAmount: 0 },
         ]);
      } else {
         setAlert(`Category "${category}" has already been added.`, "danger");
      }
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

      const totalAllocated = updatedCategories.reduce(
         (sum, cat) => sum + cat.budgetAmount,
         0
      );
      setRemainingBudget(totalBudget - totalAllocated);
   };

   const handleRemoveCategory = (categoryName) => {
      const updatedCategories = selectedCategories.filter(
         (cat) => cat.name !== categoryName
      );
      setSelectedCategories(updatedCategories);

      const totalAllocated = updatedCategories.reduce(
         (sum, cat) => sum + cat.budgetAmount,
         0
      );
      setRemainingBudget(getTotalBudget() - totalAllocated);
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
            selectedCategories={selectedCategories}
         />
         <UserInput onSubmit={handleUserInput} />
         <AdjustBudget
            categories={selectedCategories}
            onSliderChange={handleSliderChange}
            onRemoveCategory={handleRemoveCategory}
            totalBudget={getTotalBudget()}
            remainingBudget={remainingBudget}
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
