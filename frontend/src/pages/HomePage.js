import React, { useEffect, useState, useContext } from "react";
import { quotes } from "../utils/quotes";
import transactionContext from "../context/transaction/transactionContext";
import accountContext from "../context/account/accountContext";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryType from "../components/category/types/CategoryType";
import authContext from "../context/auth/authContext";

const HomePage = () => {
   const [currentQuote, setCurrentQuote] = useState("");
   const [name, setName] = useState("");
   const { syncTransactions } = useContext(transactionContext);
   const { accounts } = useContext(accountContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const { user } = useContext(authContext);

   useEffect(() => {
      const randomIndex = Math.floor(Math.random() * quotes.length);
      setCurrentQuote(quotes[randomIndex]);
   }, []);

   useEffect(() => {
      setName(user.name);
   }, [user]);

   const fetchTransactions = async () => {
      // const accountIds = accounts.map((account) => account.id);
      const accountIds = ["pyrbOAvyPJtyMRkNPjg7c1K5nJzywJs3d7oZL"];
      await syncTransactions(accountIds);
   };

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md text-center mb-8">
               <h1 className="text-2xl md:text-3xl font-bold mb-4 text-white">
                  Welcome <span className="text-indigo-500">{name}</span>
               </h1>
               <h2 className="text-4xl md:text-5xl font-bold text-white">
                  Let's Start Budgeting
               </h2>
               <button
                  className="bg-indigo-600 hover:bg-indigo-700 duration-150 text-white font-bold py-2 px-4 rounded mt-4"
                  onClick={fetchTransactions}
               >
                  Sync Transactions
               </button>
            </div>
            <div className="flex justify-center space-x-4 w-full border-2 border-red-500">
               {categoryTypes.map((categoryType) => (
                  <CategoryType
                     key={categoryType.id}
                     categoryType={categoryType}
                  />
               ))}
            </div>
         </div>
      </div>
   );
};

export default HomePage;
