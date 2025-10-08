import React, { useContext, useEffect, useState } from "react";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";
import { useNavigate, useSearchParams } from "react-router-dom";
import CategoryPerformanceContext from "../../context/category/performances/categoryPerformanceContext";
import categoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";

/**
 * Component used to represent a BudgetOverview in our BudgetPerformance entity
 *
 * @param overview
 *          - BudgetOverview to generate component for
 * @returns
 *          - BudgetOverview component
 */
const BudgetOverview = ({ overview, month, year }) => {
   const {
      overviewType,
      totalSpent,
      totalAmountAllocated,
      totalPercentUtilized,
      totalAmountSaved,
      savedAmountAttributesTotal,
   } = overview;


   const navigate = useNavigate();
   const [pieData, setPieData] = useState([])
   const [categoryMap, setCategoryMap] = useState({})
   const [currentTypeId, setCurrentTypeId] = useState(null);

   const { category_performances } = useContext(CategoryPerformanceContext)
   const { categories, fetchCategories } = useContext(categoryContext)
   const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext)


   /**
    * Retrieve category name based on Cateogry ID 
    * 
    * @param id 
    *          - category id 
    */
   const getCategoryName = (id) => {
      return categoryMap[id] || "Unknown";
   }

   // fetch category types if page is refreshed 
   useEffect(() => {
      const fetch = async () => {
         fetchCategoryTypes()
      }

      if (!categoryTypes || categoryTypes.length == 0) {
         fetch()
      }

   }, [categoryTypes])

   // set category type ID 
   useEffect(() => {
      if (!categoryTypes) {
         return;
      }

      let currCategoryTypename = convertToNormalCase(overviewType)
      let type = categoryTypes
         .find((t) => t.name == currCategoryTypename);

      if (type) {
         setCurrentTypeId(type.categoryTypeId)
      }

   }, [categoryTypes])

   // generate category mapping 
   useEffect(() => {
      if (!categories || categories.length === 0) return;

      const mapping = {};
      categories.forEach((cat) => {
         mapping[cat.categoryId] = cat.name;
      });
      setCategoryMap(mapping);
   }, [categories]);

   // fetch user categories if page is refreshed 
   useEffect(() => {
      const fetch = async () => {
         fetchCategories()
      }

      if (!categories || categories.length == 0) {
         console.log('Fetching Categories!')
         fetch()
      }

   }, [categories])

   // generate relevant pie chart data when category performances refreshed
   useEffect(() => {
      if (!category_performances || category_performances.length == 0 || currentTypeId == null) {
         return;
      }

      // filter category performances by those corresponding to category type 
      const filteredPerformances = category_performances
         .filter((curr) => curr.categoryTypeId == currentTypeId);

      const currPieData = filteredPerformances.map((performance) => ({
         name: getCategoryName(performance.categoryId),
         value: parseFloat(performance.totalSpend.toFixed(2))
      }))
      setPieData(currPieData)

   }, [category_performances, currentTypeId])

   const COLORS = [
      "#4f46e5",
      "#6366f1",
      "#818cf8",
      "#a5b4fc",
      "#c7d2fe",
      "#60a5fa",
      "#3b82f6",
      "#2563eb",
      "#1d4ed8",
      "#1e40af",
   ];

   const getProgressBarColor = () => {
      const percentage = totalPercentUtilized * 100;
      if (percentage <= 50) {
         return "bg-green-500";
      } else if (percentage <= 70) {
         return "bg-yellow-500";
      } else if (percentage <= 90) {
         return "bg-orange-500";
      } else {
         return "bg-red-500";
      }
   };

   const getSpentColor = () => {
      const percentage = totalPercentUtilized * 100;
      if (percentage <= 50) {
         return "text-green-500";
      } else if (percentage <= 70) {
         return "text-yellow-500";
      } else if (percentage <= 90) {
         return "text-orange-500";
      } else {
         return "text-red-500";
      }
   };

   const convertToNormalCase = (str) => {
      return str
         .toLowerCase()
         .split(" ")
         .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
         .join(" ");
   };

   const getTextColor = (value) => {
      return value >= 0 ? "text-green-500" : "text-red-500";
   };

   const getOverUnderText = (value) => {
      return value >= 0 ? "Under" : "Over";
   };

   return (
      <div className="relative bg-white rounded-lg shadow-md p-6 mx-4 w-full lg:w-2/3 mb-14 xs:p-3 xs:mx-2 xs:mb-8">
         <h3 className="text-2xl font-bold mb-5 text-center xs:text-xl xs:mb-3">
            {convertToNormalCase(overviewType)} Overview
         </h3>
         <div className="text-center mb-2 font-semibold text-xl xs:text-base">
            Spent{" "}
            <span className={`font-extrabold ${getSpentColor()}`}>
               ${totalSpent.toFixed(2)}
            </span>{" "}
            out of allocated
            <span className="text-black font-bold">
               {" "}
               ${totalAmountAllocated.toFixed(2)}
            </span>
         </div>
         <div className="w-full bg-gray-300 rounded-full h-4 mb-4 xs:h-3 xs:mb-2">
            <div
               className={`h-4 rounded-full transition-all duration-500 ease-in-out ${getProgressBarColor()} xs:h-3`}
               style={{
                  width: `${totalPercentUtilized * 100 > 100
                     ? 100
                     : totalPercentUtilized * 100
                     }%`,
               }}
            ></div>
         </div>
         <div className="text-center font-semibold text-lg mb-4 xs:text-base xs:mb-2">
            Budget Utilization:{" "}
            <span className="font-bold">
               {(totalPercentUtilized * 100).toFixed(2)}%
            </span>
         </div>
         <div className="text-center font-semibold text-lg mb-4 xs:text-base xs:mb-2">
            Amount {getOverUnderText(savedAmountAttributesTotal)} Budget:{" "}
            <span
               className={`font-bold ${getTextColor(
                  savedAmountAttributesTotal
               )}`}
            >
               ${Math.abs(savedAmountAttributesTotal).toFixed(2)}
            </span>
         </div>
         <div className="text-center font-semibold text-lg mb-4 xs:text-base xs:mb-2">
            Total Amount Saved:{" "}
            <span className={`font-bold ${getTextColor(totalAmountSaved)}`}>
               ${totalAmountSaved.toFixed(2)}
            </span>
         </div>
         {pieData.length > 0 && totalSpent > 0 && (
            <ResponsiveContainer width="100%" height={200}>
               <PieChart>
                  <Pie
                     data={pieData}
                     cx="50%"
                     cy="50%"
                     labelLine={false}
                     outerRadius={80}
                     fill="#8884d8"
                     dataKey="value"
                  >
                     {pieData.map((entry, index) => (
                        <Cell
                           key={`cell-${index}`}
                           fill={COLORS[index % COLORS.length]}
                        />
                     ))}
                  </Pie>
                  <Tooltip formatter={(value, name) => [`$${value.toFixed(2)}`, name]} />
               </PieChart>
            </ResponsiveContainer>
         )}
         {overviewType != "GENERAL" &&
            <div className="flex justify-center mt-4">
               <button
                  className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-1 px-2 rounded focus:outline-none focus:shadow-outline"
                  type="submit"
                  onClick={() => navigate(`/${overviewType.toLowerCase()}/analysis/${month.toLowerCase()}/${year}`)}
               >
                  View Spending Analysis
               </button>
            </div>
         }
      </div>
   );
};

export default BudgetOverview;
