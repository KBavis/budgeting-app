import React, { useContext, useState } from "react";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";

/**
 * Modal to handle renaming a Transaction
 *
 * @param transaction
 *          - Transaction to be renamed
 *  @param onClose
 *          - Function to close modal
 */
const RenameTransaction = ({ onClose, transaction }) => {
   // Global State
   const { setAlert } = useContext(AlertContext);
   const { renameTransaction } = useContext(transactionContext);

   // Local State for New Name
   const [newName, setNewName] = useState("");

   // Functionality to handle input change
   const handleInputChange = (event) => {
      setNewName(event.target.value);
   };

   // Functionality to confirm the rename
   const onConfirm = () => {
      if (newName.trim() === "") {
         setAlert("Please enter a valid name", "danger");
         return;
      }

      renameTransaction(transaction.transactionId, newName);
      onClose();
   };

   return (
      <div className="fixed inset-0 flex items-center justify-center z-[500] backdrop-blur-sm overflow-y-auto">
         <div className="bg-white p-8 rounded-lg shadow-lg w-11/12 md:w-2/3 lg:w-1/3 flex flex-col justify-between max-h-[85%] xs:p-4">
            <div>
               <div className="flex flex-col items-center mb-4 xs:mb-2">
                  <h2 className="text-3xl font-extrabold text-indigo-600 text-center mb-2 xs:text-2xl">
                     Rename Transaction
                  </h2>
                  <p className="text-center text-gray-700 xs:text-sm">
                     To rename the transaction, please enter the new name below.
                  </p>
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
                  <div className="ml-2 xs:ml-1">
                     <p className="font-bold text-lg xs:text-base">{transaction.name}</p>
                     <p className="text-gray-500 xs:text-sm">${transaction.amount}</p>
                  </div>
               </div>
            </div>
            <div className="overflow-y-auto flex-1">
               <div className="mb-4 border border-gray-300 rounded-lg p-3 flex items-center justify-center bg-indigo-100 xs:p-2">
                  <div className="relative flex items-center w-1/2 xs:w-full">
                     <input
                        type="text"
                        value={newName}
                        onChange={handleInputChange}
                        placeholder="New Name"
                        className="w-full border pl-2 pr-2 py-1 border-gray-300 rounded-md focus:outline-none focus:border-indigo-500 xs:text-sm"
                     />
                  </div>
               </div>
            </div>
            <div className="flex justify-between mt-4 xs:mt-2">
               <button
                  onClick={onClose}
                  className="modal-button-cancel px-4 py-2 mr-2 text-white bg-red-500 rounded hover:bg-red-600 xs:px-3 xs:py-1 xs:text-sm"
               >
                  Cancel
               </button>
               <button
                  onClick={onConfirm}
                  className="modal-button-confirm px-4 py-2 text-white bg-green-500 rounded hover:bg-green-600 xs:px-3 xs:py-1 xs:text-sm"
               >
                  Confirm
               </button>
            </div>
         </div>
      </div>
   );
};

export default RenameTransaction;
