import React, { useEffect, useState, useContext, useRef } from "react";
import transactionContext from "../context/transaction/transactionContext";
import accountContext from "../context/account/accountContext";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryType from "../components/category/types/CategoryType";
import authContext from "../context/auth/authContext";
import MiscellaneousTransactions from "../components/category/MiscellaneousTransactions";
import categoryContext from "../context/category/categoryContext";
import IncomeContext from "../context/income/incomeContext";
import Loading from "../components/util/Loading";

/**
 * Home Page of our Application that users will first see after Authenticating
 */
const HomePage = () => {
   //Local State
   const [name, setName] = useState("");
   const [loading, setLoading] = useState(false);
   // const [initalFetch, setInitalFetch] = useState(false); //boolean for determining if we did our inital fetch / need to do initial fetch
   const initalFetchRef = useRef(false);

   //Global States
   const {
      syncTransactions,
      fetchTransactions,
      transactions,
      loading: transactionsLoading,
      setLoading: setTransactionLoading,
   } = useContext(transactionContext);
   const {
      accounts,
      fetchAccounts,
      loading: accountsLoading,
      setLoading: setAccountsLoading,
   } = useContext(accountContext);
   const {
      categoryTypes,
      fetchCategoryTypes,
      setLoading: setCategoryTypesLoading,
      loading: categoryTypesLoading,
   } = useContext(categoryTypeContext);
   const { user } = useContext(authContext);
   const {
      categories,
      fetchCategories,
      setLoading: setCategoriesLoading,
      loading: categoriesLoading,
   } = useContext(categoryContext);
   const {
      incomes,
      fetchIncomes,
      setLoading: setIncomesLoading,
      loading: incomesLoading,
   } = useContext(IncomeContext);

   // Set Authenticated User's Name
   useEffect(() => {
      setName(user.name);
   }, [user]);

   //Sync Transactions for Added Accounts
   const fetchUpdatedTransactions = async () => {
      const accountIds = accounts.map((account) => account.accountId);
      console.log(`Syncing Transactions for AccountIds`, accountIds);
      await syncTransactions(accountIds);
   };

   //Fetch All Accounts
   const getAccounts = async () => {
      if (!accounts || accounts.length === 0) {
         //Fetch All User Accounts
         console.log("Fetching accounts...");
         setAccountsLoading();
         await fetchAccounts();
      }
   };

   //Fetch All Category Types
   const getCategoryTypes = async () => {
      if (!categoryTypes || categoryTypes.length === 0) {
         //Fetch All Category Types
         console.log("Fetching Category Types...");
         setCategoryTypesLoading();
         await fetchCategoryTypes();
      }
   };

   //Fetch All Incomes
   const getIncomes = async () => {
      if (!incomes || incomes.length === 0) {
         //Fetch All Incomes
         console.log("Fetching Incomes...");
         setIncomesLoading();
         await fetchIncomes();
      }
   };

   //Fetch All Categories
   const getCategories = async () => {
      if (!categories || categories.length === 0) {
         //Fetch All Categories
         console.log("Fetching Categories...");
         setCategoriesLoading();
         await fetchCategories();
      }
   };

   //Fetch All Transactions
   const getTransactions = async () => {
      if (!transactions || transactions.length === 0) {
         //Fetch All Transactions
         console.log("Fetching Transactions...");
         setTransactionLoading();
         await fetchTransactions();
      }
   };

   //Fetch All Entities from Backend initial Component Mounting
   useEffect(() => {
      console.log(
         `Component Mounted! Initial Fetch Value : ${initalFetchRef.current}`
      );
      //Only Fetch if Initial Fetch Is False
      if (!initalFetchRef.current) {
         getAccounts();
         getIncomes();
         getCategories();
         getCategoryTypes();
         initalFetchRef.current = true;
      }
   }, []);

   //Trigger Fetching of Transactions When Accounts loaded into Context
   useEffect(() => {
      if (accounts && accounts.length > 0) {
         getTransactions();
      }
   }, [accounts]);

   // Determine if we are still loading in all our entities from REST API
   useEffect(() => {
      setLoading(
         transactionsLoading &&
            incomesLoading &&
            categoriesLoading &&
            categoryTypesLoading &&
            accountsLoading
      );
   }, [
      transactionsLoading,
      incomesLoading,
      categoriesLoading,
      categoryTypesLoading,
      accountsLoading,
   ]);

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md text-center mb-8 mt-8 xxl:mt-4">
               <h1 className="text-2xl md:text-3xl font-bold mb-4 text-white">
                  Welcome <span className="text-indigo-500">{name}</span>
               </h1>
               <h2 className="text-4xl mb-3 md:text-5xl font-bold text-white">
                  Let's Start Budgeting
               </h2>
               <button
                  className="bg-indigo-600 hover:bg-indigo-700 duration-150 text-white font-bold py-2 px-4 rounded mt-4"
                  onClick={fetchUpdatedTransactions}
               >
                  Sync Transactions
               </button>
            </div>
            <div className="flex justify-center space-x-4 w-full mb-5">
               {!loading ? (
                  categoryTypes.map((categoryType) => (
                     <CategoryType
                        key={categoryType.categoryTypeId}
                        categoryType={categoryType}
                     />
                  ))
               ) : (
                  <Loading />
               )}
            </div>
            <MiscellaneousTransactions />
         </div>
      </div>
   );
};

export default HomePage;
