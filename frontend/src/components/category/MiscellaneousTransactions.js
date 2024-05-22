import React, { useContext, useEffect, useState } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import Transaction from "../transaction/Transaction";
import { useDrop } from "react-dnd";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";

const MiscellaneousTransactions = () => {
   const [miscTransactions, setMiscTransactions] = useState([]);
   const { transactions, removeTransactionCategory } =
      useContext(transactionContext);
   const [{ canDrop, isOver }, drop] = useDrop(() => ({
      accept: "transaction",
      drop: (item) => {
         removeTransactionCategory(item.transaction.transactionId);
      },
      collect: (monitor) => ({
         isOver: !!monitor.isOver(),
         canDrop: !!monitor.canDrop(),
      }),
   }));

   const [currentPage, setCurrentPage] = useState(0);
   const itemsPerPage = 8;
   const totalPages = Math.ceil(miscTransactions.length / itemsPerPage);

   useEffect(() => {
      const miscellaneousTransactions = transactions.filter(
         (transaction) => transaction.category === null
      );
      setMiscTransactions(miscellaneousTransactions);
   }, [transactions]);

   const handlePageChange = (delta) => {
      const newPage = currentPage + delta;
      if (newPage >= 0 && newPage < totalPages) {
         setCurrentPage(newPage);
      }
   };

   const displayedTransactions = miscTransactions.slice(
      currentPage * itemsPerPage,
      (currentPage + 1) * itemsPerPage
   );

   return transactions && transactions.length > 0 ? (
      <div
         ref={drop}
         className={`bg-white rounded-lg shadow-md p-4 w-1/2 mt-4 text-center ${
            isOver ? "bg-green-200" : canDrop ? "bg-green-100" : ""
         }`}
      >
         <h3 className="text-xl font-bold mb-1">Miscellaneous Transactions</h3>
         {miscTransactions.length > 0 && (
            <p className="mb-10">
               Please drag and drop the following Transactions to their
               respective Category.
            </p>
         )}
         <div className="grid grid-cols-4 gap-4 transition-all duration-500 ease-in-out delay-300">
            {displayedTransactions.map((transaction) => (
               <div
                  key={transaction.transactionId}
                  className="w-full hover:scale-105 hover:duration-100"
               >
                  <Transaction transaction={transaction} />
               </div>
            ))}
         </div>
         {totalPages > 1 && (
            <div className="flex justify-center mt-4">
               {currentPage > 0 && (
                  <button
                     className="bg-indigo-500 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                     onClick={() => handlePageChange(-1)}
                  >
                     <FaChevronLeft />
                  </button>
               )}
               <p className="mx-4 text-gray-700">
                  Page {currentPage + 1} of {totalPages}
               </p>
               {currentPage < totalPages - 1 && (
                  <button
                     className="bg-indigo-500 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                     onClick={() => handlePageChange(1)}
                  >
                     <FaChevronRight />
                  </button>
               )}
            </div>
         )}
      </div>
   ) : (
      ""
   );
};

export default MiscellaneousTransactions;
