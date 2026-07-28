import React from "react";
import CategoryCreationContent from "../components/category/CategoryCreationContent";

import StepProgress from "../components/layout/StepProgress";

/**
 * Page to allow user to add corresponding categories to a specific bucket (Needs, Wants, Investments)
 *
 * @param categoryType
 *          - Bucket name (Needs, Wants, Investments)
 */
const CategoryCreationPage = ({ categoryType }) => {
   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center px-4 py-8">
         {/* Step Progress */}
         <StepProgress currentStep={5} totalSteps={5} subTitle={`${categoryType} Setup`} />

         {/* Main Card */}
         <div className="max-w-xl w-full bg-slate-900/80 backdrop-blur-md border border-slate-700/80 rounded-2xl shadow-2xl p-8 animate-slide-up text-center xs:p-6">
            <h1 className="text-3xl font-extrabold text-white mb-1 xs:text-2xl">
               Setup <span className="text-brand-400">{categoryType}</span> Categories
            </h1>
            <p className="text-xs text-slate-400 mb-6">
               Select or add specific spending categories under <strong className="text-slate-200">{categoryType}</strong> and assign your target monthly budget.
            </p>

            <CategoryCreationContent categoryType={categoryType} />
         </div>
      </div>
   );
};

export default CategoryCreationPage;
