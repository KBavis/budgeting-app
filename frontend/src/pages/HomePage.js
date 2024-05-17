import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import { quotes } from "../utils/quotes";
import transactionContext from "../context/transaction/transactionContext";
import accountContext from "../context/account/accountContext";

/**
 * Home Page will contain main functionality of our application
 */
const HomePage = () => {
   // Local State
   const [currentQuote, setCurrentQuote] = useState("");

   // Access the syncTransactions method from the TransactionContext
   const { syncTransactions } = useContext(transactionContext);

   // Access the account IDs from the AccountContext
   const { accounts } = useContext(accountContext);

   // Use Effect to randomize the quote displayed via our Home Page
   useEffect(() => {
      const randomIndex = Math.floor(Math.random() * quotes.length);
      setCurrentQuote(quotes[randomIndex]);
   }, []);

   const fetchTransactions = async () => {
      //TODO: Change this logic to fetch all relevant account IDs from Context once we update ConnectAccount endpoint to incldue Account IDs
      const accountIds = ["<accountId>"];
      await syncTransactions(accountIds);
   };

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="w-32 bg-gray-800 text-white p-4">
            {/* TODO: Add sidebar icons */}
         </div>
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md text-center">
               <h1 className="text-4xl md:text-6xl font-bold mb-4 text-white">
                  Welcome to Bavis Budgeting
               </h1>
               <p className="text-lg md:text-xl mb-8 text-gray-400 italic">
                  {currentQuote}
               </p>
               <button
                  className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
                  onClick={fetchTransactions}
               >
                  Sync Transactions
               </button>
               {/* TODO: Add content for the home page */}
            </div>
         </div>
      </div>
   );
};

export default HomePage;
