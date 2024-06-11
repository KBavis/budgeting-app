import React, { useContext, useState } from "react";
import AlertContext from "../../context/alert/alertContext";
import transactionContext from "../../context/transaction/transactionContext";

/**
 *
 * @param {*} param0
 * @returns
 */
const ReduceTransaction = ({ onClose, transaction }) => {
   //Global State
   const { setAlert } = useContext(AlertContext);
   const { reduceTransactionAmount } = useContext(transactionContext);

   // Local State for Reduced Amount
   const [reducedAmount, setReducedAmount] = useState("");

   // Functionality to handle input change
   const handleInputChange = (event) => {
      setReducedAmount(event.target.value);
   };

   // Functionality to confirm the reduction
   const onConfirm = () => {
      const reducedAmountFloat = parseFloat(reducedAmount);

      if (isNaN(reducedAmountFloat) || reducedAmountFloat <= 0) {
         setAlert("Please enter a valid amount.", "danger");
         return;
      }

      if (reducedAmountFloat >= parseFloat(transaction.amount)) {
         setAlert(
            "The reduced amount must be less than the original transaction amount.",
            "danger"
         );
         return;
      }

      reduceTransactionAmount(transaction.transactionId, reducedAmountFloat);
      onClose();
   };

   return (
      <div className="fixed inset-0 flex items-center justify-center z-[500] backdrop-blur-sm overflow-y-auto">
         <div className="bg-white p-8 rounded-lg shadow-lg w-11/12 md:w-2/3 lg:w-1/3 flex flex-col justify-between max-h-[85%]">
            <div>
               <div className="flex justify-between items-center mb-4">
                  <div>
                     <h2 className="text-3xl font-extrabold text-indigo-600 text-center mb-2">
                        Reduce Transaction Amount
                     </h2>
                     <p className="text-center text-gray-700">
                        To reduce the transaction amount, please enter the new
                        amount below. Ensure that the new amount is less than
                        the original transaction amount.
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
               <div className="mb-4 border border-gray-300 rounded-lg p-3 flex items-center justify-center bg-indigo-100">
                  <div className="relative flex items-center w-1/2">
                     <span className="absolute left-2 text-gray-500">$</span>
                     <input
                        type="text"
                        value={reducedAmount}
                        onChange={handleInputChange}
                        placeholder="New Amount"
                        className="w-full border pl-5 pr-2 py-1 border-gray-300 rounded-md focus:outline-none focus:border-indigo-500"
                     />
                  </div>
               </div>
            </div>
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

export default ReduceTransaction;
