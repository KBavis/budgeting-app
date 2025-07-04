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
  const { updateCategory, renameTransaction, reduceTransactionAmount, deleteTransaction } = useContext(transactionContext);

  const [selectedCategory, setSelectedCategory] = useState(null);
  const [confirmationVisible, setConfirmationVisible] = useState(false);
  
  // Edit states
  const [isEditingName, setIsEditingName] = useState(false);
  const [isEditingAmount, setIsEditingAmount] = useState(false);
  const [editedName, setEditedName] = useState(transaction.name);
  const [editedAmount, setEditedAmount] = useState(transaction.amount);
  
  // Local transaction state to reflect changes
  const [currentTransaction, setCurrentTransaction] = useState(transaction);

  const handleCategorySelect = (category) => {
    setSelectedCategory(category);
    setConfirmationVisible(true);
  };

  const handleConfirm = () => {
    updateCategory(currentTransaction.transactionId, selectedCategory.categoryId, true);
    setAlert("Category assigned successfully", "success");
    setSelectedCategory(null);
    setConfirmationVisible(false);
    onTransactionComplete(); 
  };

  const handleNameEdit = () => {
    setIsEditingName(true);
  };

  const handleNameCancel = () => {
    setIsEditingName(false);
    setEditedName(currentTransaction.name);
  };

  const handleNameConfirm = () => {
    if (editedName.trim() === "") {
      setAlert("Please enter a valid name", "danger");
      return;
    }
    
    renameTransaction(currentTransaction.transactionId, editedName);
    setCurrentTransaction({ ...currentTransaction, name: editedName });
    setIsEditingName(false);
    setAlert("Transaction name updated successfully", "success");
  };

  const handleAmountEdit = () => {
    setIsEditingAmount(true);
  };

  const handleAmountCancel = () => {
    setIsEditingAmount(false);
    setEditedAmount(currentTransaction.amount);
  };

  const handleAmountConfirm = () => {
    const reducedAmountFloat = parseFloat(editedAmount);

    if (isNaN(reducedAmountFloat) || reducedAmountFloat <= 0) {
      setAlert("Please enter a valid amount.", "danger");
      return;
    }

    if (reducedAmountFloat >= parseFloat(transaction.amount)) {
      setAlert("The new amount must be less than the original transaction amount.", "danger");
      return;
    }

    reduceTransactionAmount(currentTransaction.transactionId, reducedAmountFloat);
    setCurrentTransaction({ ...currentTransaction, amount: editedAmount });
    setIsEditingAmount(false);
    setAlert("Transaction amount updated successfully", "success");
  };

  const handleSkipTransaction = () => {
    if (window.confirm("Are you sure you want to skip this transaction? It will be deleted and not assigned to any category.")) {
      deleteTransaction(currentTransaction.transactionId);
      onTransactionComplete();
    }
  };

  return (
    <>
      {/* Blurred semi-transparent background overlay */}
      <div className="fixed inset-0 z-40 backdrop-blur-sm bg-white/30"></div>

      {/* Modal content */}
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="bg-white p-8 rounded-lg shadow-lg w-11/12 md:w-2/3 lg:w-1/3 max-h-[85%] overflow-y-auto xs:p-4">
          <h2 className="text-3xl font-extrabold text-indigo-600 mb-4 text-center xs:text-2xl">
            Previous Month Transactions
          </h2>
          <p className="text-center font-bold text-sm mb-5 xs:text-xs">
            Finalize the allocation of Categories to your Transactions to complete last month's budget!
          </p>
          
          {/* Transaction Details with Edit Functionality */}
          <div className="flex items-center mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50 xs:p-3">
            <img
              src={
                currentTransaction.logoUrl ||
                "https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
              }
              alt="Logo"
              className="w-12 h-12 rounded-full mr-4 xs:w-8 xs:h-8 xs:mr-2"
            />
            <div className="flex-1">
              {/* Editable Name */}
              <div className="flex items-center mb-2">
                {isEditingName ? (
                  <div className="flex items-center flex-1">
                    <input
                      type="text"
                      value={editedName}
                      onChange={(e) => setEditedName(e.target.value)}
                      className="flex-1 border border-gray-300 rounded-md px-2 py-1 focus:outline-none focus:border-indigo-500 xs:text-sm"
                    />
                    <button
                      onClick={handleNameConfirm}
                      className="ml-2 text-green-600 hover:text-green-800 xs:text-sm"
                      title="Confirm"
                    >
                      ✓
                    </button>
                    <button
                      onClick={handleNameCancel}
                      className="ml-1 text-red-600 hover:text-red-800 xs:text-sm"
                      title="Cancel"
                    >
                      ✕
                    </button>
                  </div>
                ) : (
                  <div className="flex items-center flex-1">
                    <p className="font-bold text-lg flex-1 xs:text-base">{currentTransaction.name}</p>
                    <button
                      onClick={handleNameEdit}
                      className="ml-2 text-indigo-600 hover:text-indigo-800 xs:text-sm"
                      title="Edit name"
                    >
                      ✏️
                    </button>
                  </div>
                )}
              </div>
              
              {/* Editable Amount */}
              <div className="flex items-center mb-2">
                {isEditingAmount ? (
                  <div className="flex items-center">
                    <span className="mr-1 xs:text-sm">$</span>
                    <input
                      type="text"
                      value={editedAmount}
                      onChange={(e) => setEditedAmount(e.target.value)}
                      className="border border-gray-300 rounded-md px-2 py-1 focus:outline-none focus:border-indigo-500 w-24 xs:w-20 xs:text-sm"
                    />
                    <button
                      onClick={handleAmountConfirm}
                      className="ml-2 text-green-600 hover:text-green-800 xs:text-sm"
                      title="Confirm"
                    >
                      ✓
                    </button>
                    <button
                      onClick={handleAmountCancel}
                      className="ml-1 text-red-600 hover:text-red-800 xs:text-sm"
                      title="Cancel"
                    >
                      ✕
                    </button>
                  </div>
                ) : (
                  <div className="flex items-center">
                    <p className="text-gray-600 font-semibold xs:text-sm">${currentTransaction.amount}</p>
                    <button
                      onClick={handleAmountEdit}
                      className="ml-2 text-indigo-600 hover:text-indigo-800 xs:text-sm"
                      title="Edit amount"
                    >
                      ✏️
                    </button>
                  </div>
                )}
              </div>
              
              <p className="text-sm text-gray-400 xs:text-xs">
                {new Date(currentTransaction.date).toLocaleDateString()}
              </p>
            </div>
          </div>

          {/* Category Selection */}
          {categoryTypes.map((type) => (
            <div key={type.categoryTypeId} className="mb-4">
              <h3 className="text-indigo-600 font-bold mb-2 xs:text-base">{type.name}</h3>
              <div className="grid grid-cols-2 gap-2 xs:grid-cols-1">
                {type.categories.map((category) => (
                  <button
                    key={category.categoryId}
                    onClick={() => handleCategorySelect(category)}
                    className="bg-indigo-900 text-white px-4 py-2 rounded hover:bg-indigo-800 xs:px-3 xs:py-1 xs:text-sm"
                  >
                    {category.name}
                  </button>
                ))}
              </div>
            </div>
          ))}

          {/* Confirmation Dialog */}
          {confirmationVisible && (
            <div className="mt-4 p-4 border rounded shadow bg-blue-50 xs:p-3">
              <p className="xs:text-sm">
                Confirm assigning{" "}
                <span className="font-bold text-indigo-600 xs:text-sm">
                  {currentTransaction.name}
                </span>{" "}
                to{" "}
                <span className="font-bold text-indigo-600 xs:text-sm">
                  {selectedCategory.name}
                </span>{" "}
                category?
              </p>
              <div className="flex justify-end mt-2">
                <button
                  onClick={handleConfirm}
                  className="bg-green-600 text-white px-4 py-1 rounded mr-2 hover:bg-green-700 xs:px-3 xs:py-1 xs:text-sm"
                >
                  Confirm
                </button>
                <button
                  onClick={() => setConfirmationVisible(false)}
                  className="bg-gray-400 text-white px-4 py-1 rounded hover:bg-gray-500 xs:px-3 xs:py-1 xs:text-sm"
                >
                  Cancel
                </button>
              </div>
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex justify-between mt-6 xs:mt-4">
            <button
              onClick={handleSkipTransaction}
              className="bg-orange-500 text-white px-4 py-2 rounded hover:bg-orange-600 xs:px-3 xs:py-1 xs:text-sm"
              title="Skip this transaction (will be deleted)"
            >
              Skip Transaction
            </button>
            <button
              onClick={onClose}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 xs:px-3 xs:py-1 xs:text-sm"
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