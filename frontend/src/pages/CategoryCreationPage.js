import React from "react";
import CategoryCreationContent from "../components/category/CategoryCreationContent";

const CategoryCreationPage = ({ categoryType }) => {
  return (
    <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center">
      <div className="max-w-md w-full text-center">
        <h1 className="text-4xl font-bold mb-8 text-white">
          Create {categoryType} Categories
        </h1>
        <CategoryCreationContent categoryType={categoryType} />
      </div>
    </div>
  );
};

export default CategoryCreationPage;
