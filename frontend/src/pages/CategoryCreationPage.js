import React from "react";
import CategoryCreationContent from "../components/category/CategoryCreationContent";

/**
 *
 * @param categoryType
 *          - Category Type we are creating Categories for (Wants, Needs, Investments )
 * @returns
 */
const CategoryCreationPage = ({ categoryType }) => {
   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center">
         <div className="max-w-2xl w-full text-center">
            <h1 className="text-4xl font-bold mb-2 text-white">
               <span className="ml-2">Add Sub-Categories</span>
               <br />
               <span>for</span>
               <br />
               <span className="text-indigo-600  font-extrabold">
                  {categoryType}
               </span>
            </h1>
            <CategoryCreationContent categoryType={categoryType} />
         </div>
      </div>
   );
};

export default CategoryCreationPage;
