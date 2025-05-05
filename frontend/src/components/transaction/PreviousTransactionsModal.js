import React, { useContext, useState } from "react";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";

const PreviousTransactionsModal = ({
  transactions,
  currentIndex,
  onClose,
  onTransactionComplete,
}) => {
  const transaction = transactions[currentIndex];
  const { categoryTypes } = useContext(categoryTypeContext);
  const { setAlert } = useContext(AlertContext);
  const { updateCategory } = useContext(transactionContext);

  const [selectedCategory, setSelectedCategory] = useState(null);
  const [confirmationVisible, setConfirmationVisible] = useState(false);

  const handleCategorySelect = (category) => {
    setSelectedCategory(category);
    setConfirmationVisible(true);
  };

  const handleConfirm = () => {
    updateCategory(transaction.transactionId, selectedCategory.categoryId, true);
    setAlert("Category assigned successfully", "success");
    setSelectedCategory(null);
    setConfirmationVisible(false);
    onTransactionComplete(); 
  };

  return (
    <>
      {/* Blurred semi-transparent background overlay */}
      <div className="fixed inset-0 z-40 backdrop-blur-sm bg-white/30"></div>

      {/* Modal content */}
      <div className="fixed inset-0 z-50 flex items-center justify-center text-">
        <div className="bg-white p-8 rounded-lg shadow-lg w-11/12 md:w-2/3 lg:w-1/3 max-h-[85%] overflow-y-auto">
          <h2 className="text-3xl font-extrabold text-indigo-600 mb-4 text-center">
            Previous Month Transactions
          </h2>
          <p className="text-center font-bold text-sm mb-5">Finalize the allocation of Categories to your Transactions to complete last month's budget!</p>
          <div className="flex items-center mb-4">
            <img
              src={
                transaction.logoUrl ||
                "https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
              }
              alt="Logo"
              className="w-10 h-10 rounded-full mr-4"
            />
            <div>
              <p className="font-bold text-lg">{transaction.name}</p>
              <p className="text-gray-600">${transaction.amount}</p>
              <p className="text-sm text-gray-400">
                {new Date(transaction.date).toLocaleDateString()}
              </p>
            </div>
          </div>

          {categoryTypes.map((type) => (
            <div key={type.categoryTypeId} className="mb-4">
              <h3 className="text-indigo-600 font-bold mb-2">{type.name}</h3>
              <div className="grid grid-cols-2 gap-2">
                {type.categories.map((category) => (
                  <button
                    key={category.categoryId}
                    onClick={() => handleCategorySelect(category)}
                    className="bg-indigo-900 text-white px-4 py-2 rounded hover:bg-indigo-800"
                  >
                    {category.name}
                  </button>
                ))}
              </div>
            </div>
          ))}

          {confirmationVisible && (
            <div className="mt-4 p-4 border rounded shadow">
              <p>
                Confirm assigning{" "}
                <span className="font-bold text-indigo-600">
                  {transaction.name}
                </span>{" "}
                to{" "}
                <span className="font-bold text-indigo-600">
                  {selectedCategory.name}
                </span>{" "}
                category?
              </p>
              <div className="flex justify-end mt-2">
                <button
                  onClick={handleConfirm}
                  className="bg-green-600 text-white px-4 py-1 rounded mr-2"
                >
                  Confirm
                </button>
                <button
                  onClick={() => setConfirmationVisible(false)}
                  className="bg-gray-400 text-white px-4 py-1 rounded"
                >
                  Cancel
                </button>
              </div>
            </div>
          )}

          <div className="flex justify-center mt-6">
            <button
              onClick={onClose}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default PreviousTransactionsModal;

