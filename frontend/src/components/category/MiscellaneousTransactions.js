import React, { useContext, useEffect, useState } from "react";
import transactionContext from "../../context/transaction/transactionContext";
import Transaction from "../transaction/Transaction";
import { useDrop } from "react-dnd";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import { CSSTransition, TransitionGroup } from "react-transition-group";

/**
 * Component that stores all Transactions with unassigned Categories
 */
const MiscellaneousTransactions = () => {
   // Local State
   const [miscTransactions, setMiscTransactions] = useState([]);
   const [currentPage, setCurrentPage] = useState(0);
   const [animateDirection, setAnimateDirection] = useState(null);

   // Constants
   const ITEMS_PER_PAGE = 8;
   const TOTAL_PAGES = Math.ceil(miscTransactions.length / ITEMS_PER_PAGE);

   // Global State
   const { transactions, removeTransactionCategory } =
      useContext(transactionContext);

   // Functionality for a user to drag/drop a Transaction on Miscellaneous Component (removing assigned category)
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

   // Filter Transactions That Should Be Displayed In Miscellaneous Transactions Component (All Transactions with Null Category)
   useEffect(() => {
      const miscellaneousTransactions = transactions.filter(
         (transaction) => transaction.category === null
      );
      setMiscTransactions(miscellaneousTransactions);
   }, [transactions]);

   // Functionality to handle changing of page of Transactions via our pagination
   const handlePageChange = (delta) => {
      const newPage = currentPage + delta;
      if (newPage >= 0 && newPage < TOTAL_PAGES) {
         setAnimateDirection(delta > 0 ? "nextPage" : "prevPage");
         setTimeout(() => {
            setCurrentPage(newPage);
         }, 300); // Match the timeout with the animation duration
      }
   };

   // Functionality to fetch Transactions to be displayed
   const displayedTransactions = miscTransactions.slice(
      currentPage * ITEMS_PER_PAGE,
      (currentPage + 1) * ITEMS_PER_PAGE
   );

   return transactions && transactions.length > 0 ? (
      <div
         ref={drop}
         className={`bg-white rounded-lg shadow-md p-4 w-full xl:w-1/2 mt-4 text-center ${
            isOver ? "bg-green-200" : canDrop ? "bg-green-100" : ""
         }`}
      >
         <h3 className="text-xl font-bold mb-1">Miscellaneous Transactions</h3>
         {miscTransactions.length > 0 && (
            <p className="mb-5">
               Please drag and drop the following Transactions to their
               respective Category.
            </p>
         )}
         <div className="grid grid-cols-1 md-xl:grid-cols-2 xl:grid-cols-4 gap-4 overflow-hidden">
            {/* Transition for scrolling through Transactions */}
            <TransitionGroup component={null}>
               {displayedTransactions.map((transaction) => (
                  <CSSTransition
                     key={transaction.transactionId}
                     classNames="slow-slide"
                     timeout={1000}
                  >
                     <div
                        className={`w-full hover:scale-105 hover:duration-100 ${animateDirection}`}
                     >
                        <Transaction transaction={transaction} />
                     </div>
                  </CSSTransition>
               ))}
            </TransitionGroup>
         </div>
         {/* Pagination */}
         {TOTAL_PAGES > 1 && (
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
                  Page {currentPage + 1} of {TOTAL_PAGES}
               </p>
               {currentPage < TOTAL_PAGES - 1 && (
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
