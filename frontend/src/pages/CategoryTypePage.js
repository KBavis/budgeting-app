import React, { useContext, useState, useEffect } from "react";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import categoryContext from "../context/category/categoryContext";
import FullCategory from "../components/category/FullCategory";

/**
 * Page for each of our create Category Types
 *
 * @param categoryType
 *          - Category Type name to create page for
 */
const CategoryTypePage = ({ categoryType }) => {
   //Global State
   const { categoryTypes } = useContext(categoryTypeContext);
   const { categories } = useContext(categoryContext);
   // Local State
   const [filteredCategories, setFilteredCategories] = useState([]);

   useEffect(() => {
      // Find the category type object that matches the provided categoryType prop
      const currentCategoryType = categoryTypes.find(
         (ct) => ct.name.toLowerCase() === categoryType.toLowerCase()
      );

      if (currentCategoryType) {
         // Filter categories that belong to the current category type
         const filtered = categories.filter(
            (category) =>
               category.categoryType.categoryTypeId ===
               currentCategoryType.categoryTypeId
         );
         setFilteredCategories(filtered);
      }
   }, [categoryTypes, categories, categoryType]);

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md text-center mb-8">
               <h1 className="text-2xl md:text-3xl font-bold mb-4 text-white">
                  Category Type:{" "}
                  <span className="text-indigo-500">{categoryType}</span>
               </h1>
               <h2 className="text-4xl md:text-5xl font-bold text-white">
                  Explore your {categoryType}
               </h2>
            </div>
            <div className="flex justify-center space-x-4 w-full mb-5">
               {filteredCategories.length > 0 ? (
                  filteredCategories.map((category) => (
                     <FullCategory
                        key={category.categoryId}
                        category={category}
                     />
                  ))
               ) : (
                  <p className="text-white">
                     No categories found for this type.
                  </p>
               )}
            </div>
         </div>
      </div>
   );
};

export default CategoryTypePage;
