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
import RenameTransaction from "../components/transaction/RenameTransaction";
import AlertContext from "../context/alert/alertContext";
import AssignCategoryModal from "../components/transaction/AssignCategoryModal";
import AddCategory from "../components/category/AddCategory";
import UpdateAllocationsModal from "../components/category/UpdateAllocationsModal";
import RenameCategory from "../components/category/RenameCategory";
import SummaryContext from "../context/summary/summaryContext";
import PreviousTransactionsModal from "../components/transaction/PreviousTransactionsModal";
import TransactionSwiper from "../components/swiping/TransactionSwiper";

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
   const [showAssignCategoryModal, setShowAssignCategoryModal] =
      useState(false);
   const [showUpdateAllocationsModal, setShowUpdateAllocationsModal] =
      useState(false);
   const [showRenameCategoryModal, setShowRenameCategoryModal] =
      useState(false);
   const [showAddCategoryModal, setShowAddCategoryModal] = useState(false);
   const [transaction, setTransaction] = useState(null);
   const [categoryType, setCategoryType] = useState(false);
   const [category, setCategory] = useState(null);
   const [dropdownVisible, setDropdownVisible] = useState(false); // State for dropdown v
   const [showPrevTransactionsModal, setShowPrevTransactionsModal] = useState(false);
   const [modalPrevMonthTransactions, setModalPrevMonthTransactions] = useState([]);
   const [isMobile, setIsMobile] = useState(window.innerWidth < 768);
   const [showTransactionSwiper, setShowTransactionSwiper] = useState(null);
   const [transactionsToAssign, setTransactionsToAssign] = useState([]);


   const initalFetchRef = useRef(false);

   //Global States
   const {
      syncTransactions,
      fetchTransactions,
      transactions,
      prevMonthTransactions,
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
   const {
      summaries,
      fetchBudgetSummaries,
      setLoading: setSummariesLoading,
      loading: summariesLoading,
   } = useContext(SummaryContext);

   // Set Authenticated User's Name
   useEffect(() => {
      if (user) {
         setName(user.name);
      }
   }, [user]);

   //Function to open RenameCategory modal
   const handleShowRenameCategoryModal = (category) => {
      setCategory(category);
      setShowRenameCategoryModal(true);
   };

   //Function to close RenameCategory modal
   const handleCloseRenameCategoryModal = () => {
      setShowRenameCategoryModal(false);
      setCategory(null);
   };

   // Function to open SplitTransaction modal
   const handleShowSplitTransactionModal = (splitTransaction) => {
      setTransaction(splitTransaction);
      setShowSplitTransactionModal(true);
   };

   //Function to open AdjustCategoryAllocation modal
   const handleShowUpdateAllocationsModal = (categoryType) => {
      setCategoryType(categoryType);
      setShowUpdateAllocationsModal(true);
   };

   //Function to close AdjustCategoryAllocation modal
   const handleCloseUpdateAllocationsModal = () => {
      setCategoryType(null);
      setShowUpdateAllocationsModal(false);
   };

   //Function to open AssignCategory modal
   const handleShowAssignCategoryModal = (transaction) => {
      setTransaction(transaction);
      setShowAssignCategoryModal(true);
   };

   //Function to open AddCategory modal
   const handleShowAddCategoryModal = () => {
      setShowAddCategoryModal(true);
   };

   //Function to close AddCategory modal
   const handleCloseAddCategoryModal = () => {
      setShowAddCategoryModal(false);
   };

   //Function to close AssignCategory modal
   const handleCloseAssignCategoryModal = () => {
      setShowAssignCategoryModal(false);
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
         setAccountsLoading();
         await fetchAccounts();
      }
   };

   //Fetch All Budget Summaries
   const getBudgetSummaries = async () => {
      if (!summaries || summaries.length === 0) {
         //Fetch All Budget Summaries
         setSummariesLoading();
         await fetchBudgetSummaries();
      }
   };

   //Fetch All Category Types
   const getCategoryTypes = async () => {
      if (!categoryTypes || categoryTypes.length === 0) {
         //Fetch All Category Types
         setCategoryTypesLoading();
         await fetchCategoryTypes();
      }
   };

   //Fetch All Incomes
   const getIncomes = async () => {
      if (!incomes || incomes.length === 0) {
         //Fetch All Incomes
         setIncomesLoading();
         await fetchIncomes();
      }
   };

   //Fetch All Categories
   const getCategories = async () => {
      if (!categories || categories.length === 0) {
         //Fetch All Categories
         setCategoriesLoading();
         await fetchCategories();
      }
   };

   //Fetch All Transactions
   const getTransactions = async () => {
      if (!transactions || transactions.length === 0) {
         //Fetch All Transactions
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
         getBudgetSummaries();
         initalFetchRef.current = true;
      }

      //Logic To Fetch Authenticated User Present in Fetch Function
      getAuthUser();
   }, []);


   // Assign Categories for Previous Month Non-Allocated Trasnactions
   useEffect(() => {

      if(prevMonthTransactions && prevMonthTransactions.length > 0) {
         setModalPrevMonthTransactions([...prevMonthTransactions]) // create copy to iterate through
         setShowPrevTransactionsModal(true);
      }

   }, [prevMonthTransactions])

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
            accountsLoading &&
            summariesLoading
      );
   }, [
      transactionsLoading,
      incomesLoading,
      categoriesLoading,
      categoryTypesLoading,
      accountsLoading,
      summariesLoading,
   ]);

   useEffect(() => {
      const handleResize = () => {
          setIsMobile(window.innerWidth < 768);
      };
      window.addEventListener('resize', handleResize);
      return () => {
          window.removeEventListener('resize', handleResize);
      };
  }, []);

  useEffect(() => {
      if (isMobile) {
          const unassigned = transactions.filter(t => !t.category || t.category.name === 'Miscellaneous');
          setTransactionsToAssign(unassigned);
          if (unassigned.length > 0 && showTransactionSwiper === null) {
              setShowTransactionSwiper(true);
          }
      }
  }, [transactions, isMobile, showTransactionSwiper]);

  

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 relative">
         {/* Drop Down For Adding Transaction/Accounts/Categories */}
         <DropdownMenu
            dropdownVisible={dropdownVisible}
            setDropdownVisible={setDropdownVisible}
            handleShowAddTransactionModal={handleShowAddTransactionModal}
            handleShowAddCategoryModal={handleShowAddCategoryModal}
            user={user} // Pass the user prop
         />
         {isMobile && transactionsToAssign.length > 0 && !showTransactionSwiper && (
            <div className="absolute top-20 right-5">
               <button
                  className="bg-green-500 border-2 border-green-500 text-xs hover:bg-transparent duration-1000 text-white font-bold py-1 px-2 rounded"
                  onClick={() => setShowTransactionSwiper(true)}
               >
                  Continue Categorization
               </button>
            </div>
         )}

         {/* Main Content */}
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12 pt-24">
            <div className="max-w-md text-center mb-16 mt-48 xxl:mt-4 xs:mt-10 xs:mb-8">
               <h1 className="text-xl md:text-2xl font-bold mb-4 text-white">
                  Welcome <span className="text-indigo-500">{name}</span>
               </h1>
               <h2 className="text-3xl md:text-4xl mb-3 font-bold text-white">
                  Let's Start Budgeting
               </h2>
               <button
                  className="bg-indigo-600 border-2 border-indigo-600 md:text-base hover:bg-transparent duration-1000 text-white font-bold py-2 px-4 rounded mt-4 xs:text-sm xs:py-1 xs:px-2"
                  onClick={fetchUpdatedTransactions}
               >
                  Sync Transactions
               </button>
            </div>
            <div className="flex flex-col md:flex-row justify-center md:space-x-4 space-y-20 md:space-y-0 w-11/12 md:w-full mb-5">
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
                        handleShowAssignCategoryModal={
                           handleShowAssignCategoryModal
                        }
                        handleShowUpdateAllocationsModal={
                           handleShowUpdateAllocationsModal
                        }
                        handleShowRenameCategoryModal={
                           handleShowRenameCategoryModal
                        }
                     />
                  ))
               ) : (
                  <Loading />
               )}
            </div>
            {!isMobile && <MiscellaneousTransactions />}
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
         {showAssignCategoryModal && (
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <AssignCategoryModal
                  onClose={handleCloseAssignCategoryModal}
                  transaction={transaction}
               />
            </div>
         )}
         {showAddCategoryModal && (
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <AddCategory onClose={handleCloseAddCategoryModal} />
            </div>
         )}
         {showUpdateAllocationsModal && (
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <UpdateAllocationsModal
                  onClose={handleCloseUpdateAllocationsModal}
                  categoryType={categoryType}
               />
            </div>
         )}
         {showPrevTransactionsModal && 
            modalPrevMonthTransactions.length > 0 && (
               <PreviousTransactionsModal
                  transactions={modalPrevMonthTransactions}
                  onClose={() => {
                     setShowPrevTransactionsModal(false);
                     setModalPrevMonthTransactions([])
                  }}
                  onTransactionComplete={() => {
                     // Remove the first transaction from the list and update the state
                     const updatedTransactions = modalPrevMonthTransactions.slice(1);
                     setModalPrevMonthTransactions(updatedTransactions);

                     // If no more transactions, close the modal
                     if (updatedTransactions.length === 0) {
                        setShowPrevTransactionsModal(false);
                     }
                  }}
               />
         )}
         {showRenameCategoryModal && (
            <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
               <RenameCategory
                  onClose={handleCloseRenameCategoryModal}
                  category={category}
               />
            </div>
         )}
         {showTransactionSwiper && (
            <TransactionSwiper
                transactions={transactionsToAssign}
                categories={categories}
                categoryTypes={categoryTypes}
                onClose={() => setShowTransactionSwiper(false)}
            />
         )}
      </div>
   );
};

export default HomePage;

