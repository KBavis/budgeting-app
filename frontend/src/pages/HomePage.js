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
import SplitTransactionModal from "../components/transaction/SplitTransaction";
import ReduceTransaction from "../components/transaction/ReduceTransaction";
import AddTransaction from "../components/transaction/AddTransaction";
import DropdownMenu from "../components/layout/Dropdown";
import Savings from "../components/category/types/Savings";
import RenameTransaction from "../components/transaction/RenameTransaction";
import AlertContext from "../context/alert/alertContext";

const HomePage = () => {
   //Local State
   const [name, setName] = useState("");
   const [loading, setLoading] = useState(false);
   const [showSplitTransactionModal, setShowSplitTransactionModal] =
      useState(false);
   const [showReduceTransactionModal, setShowReduceTransactionModal] =
      useState(false);
   const [showAddTransactionModal, setShowAddTransactionModal] =
      useState(false);
   const [showRenameTransactionModal, setShowRenameTransactionModal] =
      useState(false);
   const [transaction, setTransaction] = useState(null);
   const [dropdownVisible, setDropdownVisible] = useState(false); // State for dropdown visibility

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
      createAccount,
      loading: accountsLoading,
      setLoading: setAccountsLoading,
   } = useContext(accountContext);
   const {
      categoryTypes,
      fetchCategoryTypes,
      setLoading: setCategoryTypesLoading,
      loading: categoryTypesLoading,
   } = useContext(categoryTypeContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
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
   const { setAlert } = useContext(AlertContext);

   // Set Authenticated User's Name
   useEffect(() => {
      if (user) {
         setName(user.name);
      }
   }, [user]);

   // Function to open SplitTransaction modal
   const handleShowSplitTransactionModal = (splitTransaction) => {
      setTransaction(splitTransaction);
      setShowSplitTransactionModal(true);
   };

   //Function to close SplitTransaction modal
   const handleCloseSplitTransactionModal = () => {
      setShowSplitTransactionModal(false);
   };

   //Function to open RenameTransaction modal
   const handleShowRenameTransactionModal = (renameTransaction) => {
      setTransaction(renameTransaction);
      setShowRenameTransactionModal(true);
   };

   //Function to close RenameTransaction modal
   const handleCloseRenameTransactionModal = () => {
      setShowRenameTransactionModal(false);
   };

   //Fucntion to open ReduceTransaction modal
   const handleShowReduceTransactionModal = (reducedTransaction) => {
      setTransaction(reducedTransaction);
      setShowReduceTransactionModal(true);
   };

   //Function to close Reduce Transaction modal
   const handleCloseReduceTransactionModal = () => {
      setShowReduceTransactionModal(false);
   };

   // Function to open AddTransaction modal
   const handleShowAddTransactionModal = () => {
      setDropdownVisible(false);
      setShowAddTransactionModal(true);
   };

   //Function to close modal
   const handleCloseAddTransactionModal = () => {
      setShowAddTransactionModal(false);
   };

   //Sync Transactions for Added Accounts
   const fetchUpdatedTransactions = async () => {
      const accountIds = accounts.map((account) => account.accountId);
      console.log(`Syncing Transactions for AccountIds`, accountIds);
      await syncTransactions(accountIds);
   };

   //Fetch Current Authenticated User
   const getAuthUser = async () => {
      //Only fetch authenticated user if not present in local state
      if (!user && localStorage.token) {
         console.log("Fetching current authenticated user...");
         await fetchAuthenticatedUser();
      }
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

      //Logic To Fetch Authenticated User Present in Fetch Function
      getAuthUser();
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
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 relative pt-6">
         {/* Drop Down For Adding Transaction/Accounts/Categories */}
         <DropdownMenu
            dropdownVisible={dropdownVisible}
            setDropdownVisible={setDropdownVisible}
            handleShowAddTransactionModal={handleShowAddTransactionModal}
            user={user} // Pass the user prop
         />

         {/* Main Content */}
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md text-center mb-8 mt-8 xxl:mt-4">
               <h1 className="text-2xl font-bold mb-4 text-white">
                  Welcome <span className="text-indigo-500">{name}</span>
               </h1>
               <h2 className="text-4xl mb-3 font-bold text-white">
                  Let's Start Budgeting
               </h2>
               <button
                  className="bg-indigo-600 border-2 border-indigo-600 md:text-base hover:bg-transparent duration-1000 text-white font-bold py-2 px-4 rounded mt-4"
                  onClick={fetchUpdatedTransactions}
               >
                  Sync Transactions
               </button>
            </div>
            <Savings />
            <div className="flex justify-center space-x-4 w-full mb-5">
               {!loading ? (
                  categoryTypes.map((categoryType) => (
                     <CategoryType
                        key={categoryType.categoryTypeId}
                        categoryType={categoryType}
                        handleShowSplitTransactionModal={
                           handleShowSplitTransactionModal
                        }
                        handleShowReduceTransactionModal={
                           handleShowReduceTransactionModal
                        }
                        handleShowRenameTransactionModal={
                           handleShowRenameTransactionModal
                        }
                     />
                  ))
               ) : (
                  <Loading />
               )}
            </div>
            <MiscellaneousTransactions />
         </div>
         {/* Modals */}
         {showSplitTransactionModal && ( // Render modal if showModal is true and selectedCategoryType is not null
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <SplitTransactionModal
                  onClose={handleCloseSplitTransactionModal}
                  transaction={transaction}
               />
            </div>
         )}
         {showReduceTransactionModal && (
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <ReduceTransaction
                  onClose={handleCloseReduceTransactionModal}
                  transaction={transaction}
               />
            </div>
         )}
         {showAddTransactionModal && ( // Render modal if showModal is true and selectedCategoryType is not null
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <AddTransaction onClose={handleCloseAddTransactionModal} />
            </div>
         )}
         {showRenameTransactionModal && (
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <RenameTransaction
                  onClose={handleCloseRenameTransactionModal}
                  transaction={transaction}
               />
            </div>
         )}
      </div>
   );
};

export default HomePage;
