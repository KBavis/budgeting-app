import React, { useState, useContext, useEffect } from "react";
import BubbleOptions from "./BubbleOptions";
import UserInput from "./UserInput";
import AdjustBudget from "./AdjustBudget";
import categoryContext from "../../context/category/categoryContext";
import IncomeContext from "../../context/income/incomeContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import AlertContext from "../../context/alert/alertContext";
import { useNavigate } from "react-router-dom";

/**
 * Main compnent for storing our CategoryCreation Content, allowing this component to be utilized for each CategoryType page
 *
 * @param categoryType
 *          - category type we are creating categories for
 */
const CategoryCreationContent = ({ categoryType }) => {
   //Local State functionality
   const [selectedCategories, setSelectedCategories] = useState([]);
   const [remainingBudget, setRemainingBudget] = useState(0);

   //Global State functionality
   const { addCategories, categories } = useContext(categoryContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const { setAlert } = useContext(AlertContext);
   const { incomes } = useContext(IncomeContext);

   const navigate = useNavigate();

   //Use Effect for updating the remaining budget
   useEffect(() => {
      const totalIncome = getTotalIncome();
      const budgetAllocationPercentage =
         getBudgetAllocationPercentage(categoryType);
      const initialRemainingBudget = totalIncome * budgetAllocationPercentage;
      setRemainingBudget(initialRemainingBudget);
   }, [incomes, categoryTypes, categoryType]);

   //Function to handle selecting a specific category
   const handleOptionSelect = (category) => {
      //Ensures this option hasn't already been selected
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

   //Function for handling when a user enters their own specific category
   const handleUserInput = (category) => {
      //Ensures this option hasn't already been selected
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

   //Function to adjust the state of the corresponidng Category budget amount/allocation based on updates to slider
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

   //Function to handle the removal of a Category when deselected
   const handleRemoveCategory = (categoryName) => {
      const updatedCategories = selectedCategories.filter(
         (cat) => cat.name !== categoryName
      );
      setSelectedCategories(updatedCategories);

      //Adjust the budget accordingly
      const totalAllocated = updatedCategories.reduce(
         (sum, cat) => sum + cat.budgetAmount,
         0
      );
      setRemainingBudget(getTotalBudget() - totalAllocated);
   };

   //Function to handle the submission of our Category budget allocations
   const handleSubmit = async () => {
      const totalPercentage = selectedCategories.reduce(
         (sum, cat) => sum + cat.budgetAllocationPercentage,
         0
      );

      //Ensures the user utilized the entirety of their budget
      if (totalPercentage !== 1) {
         setAlert(
            "You must utilize exactly 100% of your allocated budget.",
            "danger"
         );
         return;
      }

      //Sets the corresponding CategoryType of each of these Categoires
      const categoriesWithType = selectedCategories.map((category) => ({
         ...category,
         categoryTypeId: getCategoryTypeId(categoryType),
      }));

      //Attempts to POST to API and navigates to next page
      try {
         await addCategories(categoriesWithType);
         setAlert(
            `${categoryType} sub-categories were added succesfully`,
            "success"
         );
         setSelectedCategories([]);

         if (categoryType === "Needs") {
            navigate("/category/wants");
         } else if (categoryType === "Wants") {
            navigate("/category/investments");
         } else if (categoryType === "Investments") {
            navigate("/home");
         }
      } catch (error) {
         setAlert("Failed to add categories", "danger");
      }
   };

   //Funnctionality to fetch corresponding CategoryType ID based on which CategoryType this content is for
   const getCategoryTypeId = (categoryTypeName) => {
      const filteredCategoryTypes = categoryTypes.filter(
         (type) => type.name.toLowerCase() === categoryTypeName.toLowerCase()
      );
      return filteredCategoryTypes.length > 0
         ? filteredCategoryTypes[0].categoryTypeId
         : null;
   };

   //Function to fetch the total income of all incomes in state
   //TODO: Move this to Global State?
   const getTotalIncome = () => {
      return incomes.reduce((total, income) => total + income.amount, 0);
   };

   //Function to fetch the budget allocation perecentage for the specified CategoryType from global state
   const getBudgetAllocationPercentage = (categoryTypeName) => {
      const categoryTypeObj = categoryTypes.find(
         (type) => type.name.toLowerCase() === categoryTypeName.toLowerCase()
      );
      return categoryTypeObj ? categoryTypeObj.budgetAllocationPercentage : 0;
   };

   //Function to determine initial total budget based on total income and allocated budget percentage
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
