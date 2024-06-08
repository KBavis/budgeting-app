import React, { useContext, useState } from "react";
import transactionContext from "../../context/transaction/transactionContext";

/**
 * Modal used to Add a Transaction indepdent from any Account
 *
 * @param onClose
 *          - Function to Close Modal
 */
const AddTransaction = ({ onClose }) => {
   //Local State
   const [transactionName, setTransactionName] = useState("");
   const [transactionAmount, setTransactionAmount] = useState("");
   const [transactionDate, setTransactionDate] = useState("");

   //Global State
   const { addTransaction } = useContext(transactionContext);

   // Functionality to close the modal
   const handleClose = () => {
      onClose();
   };

   // Function to handle form submission
   const handleSubmit = (e) => {
      e.preventDefault();
      handleClose();
      const transactionDto = {
         updatedName: transactionName,
         updatedAmount: transactionAmount,
         date: transactionDate,
      };
      addTransaction(transactionDto);
   };

   return (
      <div className="fixed inset-0 flex items-center justify-center z-40 backdrop-blur-sm overflow-y-auto">
         <div className="bg-white p-8 rounded shadow-lg w-1/4 flex flex-col justify-between">
            <div className="flex justify-between items-center mb-4">
               <div>
                  <h2 className="text-3xl font-extrabold text-indigo-600 text-center flex-grow mb-2">
                     Add Transaction
                  </h2>
                  <p className="text-center">
                     To add a Transaction not associated with any added
                     financial institution, simply fill out the following
                     information and submit!
                  </p>
               </div>
            </div>
            <form onSubmit={handleSubmit}>
               <div className="mb-4">
                  <label
                     htmlFor="transactionName"
                     className="block text-sm font-medium text-gray-700"
                  >
                     Transaction Name
                  </label>
                  <input
                     type="text"
                     id="transactionName"
                     value={transactionName}
                     onChange={(e) => setTransactionName(e.target.value)}
                     className="mt-1 p-2 w-full border border-gray-300 rounded-md"
                  />
               </div>
               <div className="mb-4">
                  <label
                     htmlFor="transactionAmount"
                     className="block text-sm font-medium text-gray-700"
                  >
                     Transaction Amount
                  </label>
                  <div className="relative">
                     <span className="absolute inset-y-0 left-0 pl-2 flex items-center text-gray-500">
                        $
                     </span>
                     <input
                        type="text"
                        id="transactionAmount"
                        value={transactionAmount}
                        onChange={(e) => setTransactionAmount(e.target.value)}
                        className="mt-1 pl-8 pr-2 py-2 w-full border border-gray-300 rounded-md focus:outline-none focus:border-indigo-500"
                     />
                  </div>
               </div>
               <div className="mb-4">
                  <label
                     htmlFor="transactionDate"
                     className="block text-sm font-medium text-gray-700"
                  >
                     Transaction Date
                  </label>
                  <input
                     type="date"
                     id="transactionDate"
                     value={transactionDate}
                     onChange={(e) => setTransactionDate(e.target.value)}
                     className="mt-1 p-2 w-full border border-gray-300 rounded-md"
                  />
               </div>
               <div className="flex justify-between">
                  <button
                     type="button"
                     onClick={handleClose}
                     className="modal-button-cancel px-4 py-2 text-white bg-red-500 rounded hover:bg-red-600"
                  >
                     Cancel
                  </button>
                  <button
                     type="submit"
                     className="modal-button-confirm px-4 py-2 ml-2 text-white bg-green-500 rounded hover:bg-green-600"
                  >
                     Confirm
                  </button>
               </div>
            </form>
         </div>
      </div>
   );
};

export default AddTransaction;
