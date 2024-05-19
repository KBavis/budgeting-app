import React, { useEffect, useState, useContext } from "react";
import { quotes } from "../utils/quotes";
import transactionContext from "../context/transaction/transactionContext";
import accountContext from "../context/account/accountContext";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryType from "../components/category/types/CategoryType";
import authContext from "../context/auth/authContext";
import MiscellaneousTransactions from "../components/category/MiscellaneousTransactions";
import categoryContext from "../context/category/categoryContext";
import transaction from "../context/transaction/initialState";
import IncomeContext from "../context/income/incomeContext";

/**
 * Home Page of our Application that users will first see after Authenticating
 */
const HomePage = () => {
   //Local State
   const [name, setName] = useState("");

   //Global State
   const { syncTransactions, transactions } = useContext(transactionContext);
   const { accounts, fetchAccounts } = useContext(accountContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const { user } = useContext(authContext);
   const { categories } = useContext(categoryContext);
   const { incomes } = useContext(IncomeContext);

   //Set Authenticated User's Name
   useEffect(() => {
      setName(user.name);
   }, [user]);

   //Function to Fetch Transactions for Added Accounts
   const fetchTransactions = async () => {
      const accountIds = accounts.map((account) => account.accountId);
      console.log(`Fetching Transactions for ${accountIds}`);
      await syncTransactions(accountIds);
   };

   //Ensure Global State has access to all Accounts, Categories, CategoryTypes, and Transactions
   useEffect(() => {
      if (!accounts) {
         //Fetch All User Accounts
         fetchAccounts();
      }

      if (!categoryTypes) {
         //Fetch All Category Types
      }

      if (!categories) {
         //Fetch All Categoories
      }

      if (!transactions) {
         //Fetch All Transactions
         syncTransactions();
      }

      if (!incomes) {
         //Fetch All Incomes
      }
   }, []);

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
            <div className="flex justify-center space-x-4 w-full mb-5">
               {categoryTypes.map((categoryType) => (
                  <CategoryType
                     key={categoryType.id}
                     categoryType={categoryType}
                  />
               ))}
            </div>
            <MiscellaneousTransactions />
         </div>
      </div>
   );
};

export default HomePage;
