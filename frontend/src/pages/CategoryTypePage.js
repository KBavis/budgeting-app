import React, { useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import categoryContext from "../context/category/categoryContext";
import DetailedCategory from "../components/category/DetailedCategory";
import { FaArrowLeft } from "react-icons/fa";
import authContext from "../context/auth/authContext";
import transactionContext from "../context/transaction/transactionContext";

const CategoryTypePage = ({ categoryType }) => {
  const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext);
  const { categories, fetchCategories } = useContext(categoryContext);
  const { user, fetchAuthenticatedUser } = useContext(authContext);
  const { transactions, fetchTransactions } = useContext(transactionContext);
  const [filteredCategories, setFilteredCategories] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const currentCategoryType = categoryTypes.find(
      (ct) => ct.name.toLowerCase() === categoryType.toLowerCase()
    );

    if (currentCategoryType) {
      const filtered = categories.filter(
        (category) =>
          category.categoryType.categoryTypeId ===
          currentCategoryType.categoryTypeId
      );
      setFilteredCategories(filtered);
    }
  }, [categoryTypes, categories, categoryType]);

  useEffect(() => {
    if (!user && localStorage.token) {
      fetchAuthenticatedUser();
    }

    if (!categoryTypes || categoryTypes.length === 0) {
      fetchCategoryTypes();
    }

    if (!categories || categories.length === 0) {
      fetchCategories();
    }

    if (!transactions || transactions.length === 0) {
      fetchTransactions();
    }
  }, []);

  const handleBackClick = () => {
    navigate("/home");
  };

  return (
    <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
      <FaArrowLeft
        className="text-3xl text-white ml-5 mt-5 hover:scale-105 hover:text-gray-200 cursor-pointer z-[500]"
        onClick={handleBackClick}
      />
      <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
        <div className="max-w-md text-center mb-8">
          <h2 className="text-4xl md:text-5xl font-bold text-white">
            Explore your{" "}
            <span className="text-indigo-600"> {categoryType} </span>
          </h2>
        </div>
        <div className="flex flex-wrap justify-center items-center w-full space-y-4">
          {filteredCategories.length > 0 ? (
            filteredCategories.map((category) => (
              <DetailedCategory key={category.categoryId} category={category} />
            ))
          ) : (
            <p className="text-white">No categories found for this type.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default CategoryTypePage;
