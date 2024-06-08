import React, { useState } from "react";

const SplitTransactionModal = ({ onClose, onConfirm, transaction }) => {
   const [splitTransactions, setSplitTransactions] = useState([
      { name: "", amount: "", removable: false },
      { name: "", amount: "", removable: false },
   ]);

   const handleAddSplitTransaction = () => {
      setSplitTransactions([
         ...splitTransactions,
         { name: "", amount: "", removable: true },
      ]);
   };

   const handleRemoveSplitTransaction = (index) => {
      if (splitTransactions[index].removable) {
         const updatedTransactions = [...splitTransactions];
         updatedTransactions.splice(index, 1);
         setSplitTransactions(updatedTransactions);
      }
   };

   const handleInputChange = (index, event) => {
      const { name, value } = event.target;
      const list = [...splitTransactions];
      list[index][name] = value;
      setSplitTransactions(list);
   };

   return (
      <div className="fixed inset-0 flex items-center justify-center z-50 backdrop-blur-sm overflow-y-auto">
         <div className="bg-white p-8 rounded shadow-lg w-1/4 h-1/2 flex flex-col justify-between">
            <div>
               <div className="flex justify-between items-center mb-4">
                  <div>
                     <h2 className="text-3xl font-extrabold text-indigo-600 text-center flex-grow mb-2">
                        Split Transaction
                     </h2>
                     <p className="text-center">
                        To split a transaction, please divide it by specifying
                        both the name and amount for each portion. Ensure that
                        the sum of all amounts matches the total amount of the
                        original transaction.
                     </p>
                  </div>
               </div>
               <div className="flex items-center justify-center mb-4 pt-5">
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
            <div className="overflow-y-auto">
               {splitTransactions.map((split, index) => (
                  <div
                     key={index}
                     className="mb-4 bg-gray-200 border-[1px] border-black relative"
                  >
                     {split.removable && (
                        <span
                           className="absolute top-0 right-0 cursor-pointer text-black text-2xl font-extrabold px-2"
                           onClick={() => handleRemoveSplitTransaction(index)}
                        >
                           &times;
                        </span>
                     )}
                     <div className="flex flex-col gap-2 items-center p-3">
                        <input
                           type="text"
                           name="name"
                           value={split.name}
                           onChange={(e) => handleInputChange(index, e)}
                           placeholder="Transaction Name"
                           className="w-full border px-2 py-1 border-gray-300 rounded-full focus:outline-none focus:border-indigo-500"
                        />
                        <input
                           name="amount"
                           value={split.amount}
                           onChange={(e) => handleInputChange(index, e)}
                           placeholder="Transaction Amount"
                           className="w-full border px-2 py-1 border-gray-300 rounded-full focus:outline-none focus:border-indigo-500"
                        />
                     </div>
                  </div>
               ))}
            </div>
            <button
               onClick={handleAddSplitTransaction}
               className="w-1/2 mx-auto font-bold mb-4 bg-indigo-600 border-2 border-indigo-600 text-white py-2 rounded duration-500 hover:text-indigo-600 hover:bg-transparent "
            >
               Add Another Transaction
            </button>
            <div className="flex justify-between mt-4">
               <button
                  onClick={onClose}
                  className="modal-button-cancel px-4 py-2 mr-2 text-white bg-red-500 rounded hover:bg-red-600"
               >
                  Cancel
               </button>
               <button
                  onClick={() => onConfirm(splitTransactions)}
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
