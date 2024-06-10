import React, { useContext, useState } from "react";
import { FaPlus, FaTimes } from "react-icons/fa"; // Importing icons
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";

const SplitTransactionModal = ({ onClose, transaction }) => {
  //Global State
  const { setAlert } = useContext(AlertContext);
  const { splitTransaction } = useContext(transactionContext);

  // Local State for Initial Split
  const [splitTransactions, setSplitTransactions] = useState([
    { name: "", amount: "", removable: false },
    { name: "", amount: "", removable: false },
  ]);

  // Functionality to Add Initial Transaction
  const handleAddSplitTransaction = () => {
    setSplitTransactions([
      ...splitTransactions,
      { name: "", amount: "", removable: true },
    ]);
  };

  // Functionality to Remove a Split Transaction
  const handleRemoveSplitTransaction = (index) => {
    if (splitTransactions[index].removable) {
      const updatedTransactions = [...splitTransactions];
      updatedTransactions.splice(index, 1);
      setSplitTransactions(updatedTransactions);
    }
  };

  // Functionality to adjust Transaction Name/Amount
  const handleInputChange = (index, event) => {
    const { name, value } = event.target;
    const list = [...splitTransactions];
    list[index][name] = value;
    setSplitTransactions(list);
  };

  // Functionality to confirm a split Transaction
  const onConfirm = () => {
    // Ensure Minimum of Two Transactions Filled Out
    const filledTransactions = splitTransactions.filter(
      (split) => split.name && split.amount
    );
    if (filledTransactions.length < 2) {
      setAlert(
        "Please ensure at least two transactions are split out.",
        "danger"
      );
      return;
    }

    //Ensure Amount of Transactions is Not More Than Original
    const totalAmount = filledTransactions.reduce(
      (total, split) => total + parseFloat(split.amount),
      0
    );

    if (totalAmount > parseFloat(transaction.amount)) {
      setAlert(
        "The total amount of split-out transactions cannot exceed the original transaction amount.",
        "danger"
      );
      return;
    }

    // Parse Transactions into TransactionDto Format and then into SplitTransactionDto
    const transactionDtos = filledTransactions.map((split) => ({
      updatedName: split.name,
      updatedAmount: parseFloat(split.amount),
    }));
    const updatedTransactions = {
      splitTransactions: transactionDtos,
    };

    splitTransaction(transaction.transactionId, updatedTransactions);
    onClose();
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center z-[500] backdrop-blur-sm overflow-y-auto">
      <div className="bg-white p-8 rounded-lg shadow-lg w-11/12 md:w-2/3 lg:w-1/3 flex flex-col justify-between max-h-[85%]">
        <div>
          <div className="flex justify-between items-center mb-4">
            <div>
              <h2 className="text-3xl font-extrabold text-indigo-600 text-center mb-2">
                Split Transaction
              </h2>
              <p className="text-center text-gray-700">
                To split a transaction, please divide it by specifying both the
                name and amount for each portion. Ensure that the sum of all
                amounts matches the total amount of the original transaction.
              </p>
            </div>
          </div>
          <div className="flex items-center justify-center mb-4 pt-2">
            <img
              src={
                transaction.logoUrl ||
                "https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
              }
              alt="Transaction Logo"
              className="w-8 h-8 rounded-full"
            />
            <div className="ml-2">
              <p className="font-bold text-lg">{transaction.name}</p>
              <p className="text-gray-500">${transaction.amount}</p>
            </div>
          </div>
        </div>
        <div className="overflow-y-auto flex-1">
          {splitTransactions.map((split, index) => (
            <div
              key={index}
              className="mb-4 border border-gray-300 rounded-lg p-3 flex items-center bg-indigo-100 relative"
            >
              {split.removable && (
                <button
                  className="absolute top-2 right-2 bg-white border border-gray-500 rounded-full p-1 shadow cursor-pointer hover:scale-105"
                  onClick={() => handleRemoveSplitTransaction(index)}
                >
                  <FaTimes className="text-red-500" />
                </button>
              )}
              <div className="flex flex-col gap-2 w-full">
                <input
                  type="text"
                  name="name"
                  value={split.name}
                  onChange={(e) => handleInputChange(index, e)}
                  placeholder="Transaction Name"
                  className="w-full border  px-2 py-1 border-gray-300 rounded-md focus:outline-none focus:border-indigo-500"
                />
                <div className="relative flex items-center">
                  <span className="absolute left-2 text-gray-500">$</span>
                  <input
                    type="text"
                    name="amount"
                    value={split.amount}
                    onChange={(e) => handleInputChange(index, e)}
                    placeholder="Transaction Amount"
                    className="w-full border pl-5 pr-2 py-1 border-gray-300 rounded-md focus:outline-none focus:border-indigo-500"
                  />
                </div>
              </div>
            </div>
          ))}
        </div>
        <button
          onClick={handleAddSplitTransaction}
          className="w-10 h-10 mx-auto font-bold mb-4 bg-indigo-600 border-2 border-indigo-600 text-white rounded-full flex items-center justify-center duration-500 hover:text-indigo-600 hover:bg-transparent"
        >
          <FaPlus />
        </button>
        <div className="flex justify-between mt-4">
          <button
            onClick={onClose}
            className="modal-button-cancel px-4 py-2 mr-2 text-white bg-red-500 rounded hover:bg-red-600"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            className="modal-button-confirm px-4 py-2 text-white bg-green-500 rounded hover:bg-green-600"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  );
};

export default SplitTransactionModal;
