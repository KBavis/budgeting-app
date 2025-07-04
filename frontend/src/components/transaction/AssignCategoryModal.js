import React, { useContext, useState, useEffect } from "react";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";

const AssignCategoryModal = ({ onClose, transaction }) => {
  // Global State
  const { categoryTypes } = useContext(categoryTypeContext);
  const { setAlert } = useContext(AlertContext);
  const { updateCategory } = useContext(transactionContext);

  // Local State
  const [needsCategories, setNeedsCategories] = useState([]);
  const [wantsCategories, setWantsCategories] = useState([]);
  const [investmentsCategories, setInvestmentsCategories] = useState([]);

  // Function to handle category selection
  const handleCategorySelection = (category) => {
    if (!category) {
      setAlert("Please select a category.", "danger");
      return;
    }

    updateCategory(transaction.transactionId, category.categoryId);
    setAlert("Category successfully assigned", "success");
    onClose();
  };

  // Filter Categories for each Category Type
  useEffect(() => {
    const needsCategoryType = categoryTypes.find(
      (ct) => ct.name.toLowerCase() === "needs"
    );
    if (needsCategoryType) {
      setNeedsCategories(needsCategoryType.categories);
    }

    const wantsCategoryType = categoryTypes.find(
      (ct) => ct.name.toLowerCase() === "wants"
    );
    if (wantsCategoryType) {
      setWantsCategories(wantsCategoryType.categories);
    }

    const investmentCategoryType = categoryTypes.find(
      (ct) => ct.name.toLowerCase() === "investments"
    );
    if (investmentCategoryType) {
      setInvestmentsCategories(investmentCategoryType.categories);
    }
  }, [categoryTypes]);

  return (
    <div className="fixed inset-0 flex text-center items-center justify-center z-[500] backdrop-blur-sm overflow-y-auto">
      <div className="bg-white p-8 rounded-lg shadow-lg w-11/12 md:w-2/3 lg:w-1/3 flex flex-col justify-between max-h-[85%] xs:p-4">
        <div>
          <div className="flex justify-center items-center mb-4 xs:mb-2">
            <div>
              <h2 className="text-3xl font-extrabold text-indigo-600 mb-2 xs:text-2xl">
                Assign Category
              </h2>
              <p className="text-center text-gray-700 xs:text-sm">
                Select a category to assign to the transaction.
              </p>
            </div>
          </div>
          <div className="flex items-center justify-center mb-4 pt-2 xs:pt-1">
            <img
              src={
                transaction.logoUrl ||
                "https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
              }
              alt="Transaction Logo"
              className="w-8 h-8 rounded-full xs:w-6 xs:h-6"
            />
            <div className="ml-2 text-left xs:ml-1">
              <p className="font-bold text-lg xs:text-base">{transaction.name}</p>
              <p className="text-gray-500 xs:text-sm">${transaction.amount}</p>
            </div>
          </div>
        </div>
        <div className="overflow-y-auto flex-1">
          {categoryTypes.map((categoryType) => (
            <div
              key={categoryType.categoryTypeId}
              className="mb-4 border-2 px-8 py-6 pt-1 rounded-lg xs:px-4 xs:py-3 xs:pt-1"
            >
              <h3 className="text-xl font-bold text-center text-indigo-600 mb-2 xs:text-lg">
                {categoryType.name}
              </h3>
              <div className="grid grid-cols-2 gap-2 xs:grid-cols-1">
                {(() => {
                  switch (categoryType.name.toLowerCase()) {
                    case "needs":
                      return needsCategories.map((category) => (
                        <button
                          key={category.categoryId}
                          onClick={() => handleCategorySelection(category)}
                          className="px-4 py-2 bg-indigo-900 rounded-lg text-white hover:bg-indigo-800 focus:outline-none xs:px-3 xs:py-1 xs:text-sm"
                        >
                          {category.name}
                        </button>
                      ));
                    case "wants":
                      return wantsCategories.map((category) => (
                        <button
                          key={category.categoryId}
                          onClick={() => handleCategorySelection(category)}
                          className="px-4 py-2 bg-indigo-900 rounded-lg text-white hover:bg-indigo-800 focus:outline-none xs:px-3 xs:py-1 xs:text-sm"
                        >
                          {category.name}
                        </button>
                      ));
                    case "investments":
                      return investmentsCategories.map((category) => (
                        <button
                          key={category.categoryId}
                          onClick={() => handleCategorySelection(category)}
                          className="px-4 py-2 bg-indigo-900 rounded-lg text-white hover:bg-indigo-800 focus:outline-none xs:px-3 xs:py-1 xs:text-sm"
                        >
                          {category.name}
                        </button>
                      ));
                    default:
                      return null;
                  }
                })()}
              </div>
            </div>
          ))}
        </div>
        <div className="flex justify-center mt-4 xs:mt-2">
          <button
            onClick={onClose}
            className="modal-button-cancel px-4 py-2 mr-2 text-white bg-red-500 rounded hover:bg-red-600 xs:px-3 xs:py-1 xs:text-sm"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default AssignCategoryModal;
