import React, { useEffect, useState, useContext, useRef } from "react";
import transactionContext from "../context/transaction/transactionContext";
import accountContext from "../context/account/accountContext";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryType from "../components/category/types/CategoryType";
import authContext from "../context/auth/authContext";
import categoryContext from "../context/category/categoryContext";
import IncomeContext from "../context/income/incomeContext";
import Loading from "../components/util/Loading";
import SplitTransactionModal from "../components/transaction/SplitTransaction";
import ReduceTransaction from "../components/transaction/ReduceTransaction";
import AddTransaction from "../components/transaction/AddTransaction";
import RenameTransaction from "../components/transaction/RenameTransaction";
import AlertContext from "../context/alert/alertContext";
import AssignCategoryModal from "../components/transaction/AssignCategoryModal";
import AddCategory from "../components/category/AddCategory";
import UpdateAllocationsModal from "../components/category/UpdateAllocationsModal";
import RenameCategory from "../components/category/RenameCategory";
import SummaryContext from "../context/summary/summaryContext";
import TransactionSwiper from "../components/swiping/TransactionSwiper";
import { FaSyncAlt, FaPlus, FaList, FaExclamationCircle } from "react-icons/fa";

const HomePage = () => {
   // Local State
   const [name, setName] = useState("");
   const [loading, setLoading] = useState(false);
   const [showSplitTransactionModal, setShowSplitTransactionModal] = useState(false);
   const [showReduceTransactionModal, setShowReduceTransactionModal] = useState(false);
   const [showAddTransactionModal, setShowAddTransactionModal] = useState(false);
   const [showRenameTransactionModal, setShowRenameTransactionModal] = useState(false);
   const [showAssignCategoryModal, setShowAssignCategoryModal] = useState(false);
   const [showUpdateAllocationsModal, setShowUpdateAllocationsModal] = useState(false);
   const [showRenameCategoryModal, setShowRenameCategoryModal] = useState(false);
   const [showAddCategoryModal, setShowAddCategoryModal] = useState(false);
   const [transaction, setTransaction] = useState(null);
   const [categoryType, setCategoryType] = useState(false);
   const [category, setCategory] = useState(null);
   const [showTransactionSwiper, setShowTransactionSwiper] = useState(null);
   const [transactionsToAssign, setTransactionsToAssign] = useState([]);

   const initalFetchRef = useRef(false);

   // Global States
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

   // Function to open RenameCategory modal
   const handleShowRenameCategoryModal = (category) => {
      setCategory(category);
      setShowRenameCategoryModal(true);
   };

   const handleCloseRenameCategoryModal = () => {
      setShowRenameCategoryModal(false);
      setCategory(null);
   };

   const handleShowSplitTransactionModal = (splitTransaction) => {
      setTransaction(splitTransaction);
      setShowSplitTransactionModal(true);
   };

   const handleCloseSplitTransactionModal = () => {
      setShowSplitTransactionModal(false);
      setTransaction(null);
   };

   const handleShowReduceTransactionModal = (reduceTransaction) => {
      setTransaction(reduceTransaction);
      setShowReduceTransactionModal(true);
   };

   const handleCloseReduceTransactionModal = () => {
      setShowReduceTransactionModal(false);
      setTransaction(null);
   };

   const handleShowAddTransactionModal = () => {
      setShowAddTransactionModal(true);
   };

   const handleCloseAddTransactionModal = () => {
      setShowAddTransactionModal(false);
   };

   const handleShowRenameTransactionModal = (renameTransaction) => {
      setTransaction(renameTransaction);
      setShowRenameTransactionModal(true);
   };

   const handleCloseRenameTransactionModal = () => {
      setShowRenameTransactionModal(false);
      setTransaction(null);
   };

   const handleShowAssignCategoryModal = (assignCategory) => {
      setTransaction(assignCategory);
      setShowAssignCategoryModal(true);
   };

   const handleCloseAssignCategoryModal = () => {
      setShowAssignCategoryModal(false);
      setTransaction(null);
   };

   const handleShowAddCategoryModal = () => {
      setShowAddCategoryModal(true);
   };

   const handleCloseAddCategoryModal = () => {
      setShowAddCategoryModal(false);
   };

   const handleShowUpdateAllocationsModal = (categoryType) => {
      setCategoryType(categoryType);
      setShowUpdateAllocationsModal(true);
   };

   const handleCloseUpdateAllocationsModal = () => {
      setShowUpdateAllocationsModal(false);
      setCategoryType(null);
   };

   const getAccounts = async () => {
      setAccountsLoading();
      await fetchAccounts();
   };

   const getTransactions = async () => {
      setTransactionLoading();
      await fetchTransactions();
   };

   const getCategories = async () => {
      setCategoriesLoading();
      await fetchCategories();
   };

   const getCategoryTypes = async () => {
      setCategoryTypesLoading();
      await fetchCategoryTypes();
   };

   const getAuthUser = async () => {
      await fetchAuthenticatedUser();
   };

   const getIncomes = async () => {
      setIncomesLoading();
      await fetchIncomes();
   };

   const getBudgetSummaries = async () => {
      setSummariesLoading();
      await fetchBudgetSummaries();
   };

   const fetchUpdatedTransactions = async () => {
      setTransactionLoading();
      const accountIds = (accounts || []).map((acc) => acc.accountId);
      await syncTransactions(accountIds);
      setAlert("Successfully Synced Account Transactions!", "success");
   };

   // Component Mount logic
   useEffect(() => {
      if (!initalFetchRef.current) {
         getAccounts();
         getIncomes();
         getCategories();
         getCategoryTypes();
         getBudgetSummaries();
         initalFetchRef.current = true;
      }
      getAuthUser();
   }, []);

   // Fetch transactions when accounts exist
   useEffect(() => {
      if (accounts && accounts.length > 0) {
         getTransactions();
      }
   }, [accounts]);

   // Determine global loading state
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

   // Merge previous month and current unassigned transactions into single swiper list
   useEffect(() => {
      const unassigned = (transactions || []).filter(t => !t.category || t.category.name === 'Miscellaneous');
      const combined = [
         ...(prevMonthTransactions || []),
         ...unassigned
      ];
      setTransactionsToAssign(combined);
      if (combined.length > 0 && showTransactionSwiper === null) {
         setShowTransactionSwiper(true);
      }
   }, [transactions, prevMonthTransactions, showTransactionSwiper]);

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-slate-100 to-indigo-100 dark:from-gray-900 dark:to-indigo-800 relative text-slate-800 dark:text-slate-100">
         {/* Main Content shifted upward */}
         <div className="flex-1 flex flex-col justify-center items-center px-4 md:px-12 pt-10">
            <div className="max-w-3xl text-center mb-8 mt-12 md:mt-8 w-full">
               <h1 className="text-xl md:text-2xl font-bold mb-1 text-slate-500 dark:text-slate-300">
                  Welcome back, <span className="text-indigo-600 dark:text-indigo-400 font-extrabold">{name}</span>
               </h1>
               <h2 className="text-3xl md:text-5xl font-black mb-6 text-slate-900 dark:text-white tracking-tight">
                  Budget Dashboard
               </h2>

               {/* Unified Segmented Action Toolbar */}
               <div className="flex flex-wrap justify-center items-center gap-3">
                  <div className="bg-white/90 dark:bg-slate-800/90 backdrop-blur-md border border-slate-200/90 dark:border-slate-700/80 rounded-2xl p-1.5 shadow-md flex items-center gap-1">
                     <button
                        className="px-3.5 py-2 rounded-xl text-xs md:text-sm font-bold text-slate-700 dark:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-700 transition-all flex items-center gap-1.5"
                        onClick={handleShowAddTransactionModal}
                     >
                        <FaPlus className="w-3 h-3 text-brand-500" />
                        <span>Transaction</span>
                     </button>
                     <div className="w-px h-5 bg-slate-300 dark:bg-slate-700" />
                     <button
                        className="px-3.5 py-2 rounded-xl text-xs md:text-sm font-bold text-slate-700 dark:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-700 transition-all flex items-center gap-1.5"
                        onClick={handleShowAddCategoryModal}
                     >
                        <FaList className="w-3 h-3 text-indigo-500" />
                        <span>Category</span>
                     </button>
                  </div>

                  <button
                     className="bg-brand-600 border border-brand-500 text-xs md:text-sm hover:bg-brand-500 duration-300 text-white font-bold py-2.5 px-5 rounded-2xl shadow-md transition-all hover:scale-105 inline-flex items-center gap-2"
                     onClick={fetchUpdatedTransactions}
                  >
                     <FaSyncAlt className="w-3.5 h-3.5" />
                     <span>Sync Transactions</span>
                  </button>

                  {transactionsToAssign.length > 0 && !showTransactionSwiper && (
                     <button
                        className="relative group bg-amber-500/10 hover:bg-amber-500/20 dark:bg-amber-500/15 dark:hover:bg-amber-500/25 text-amber-700 dark:text-amber-300 border border-amber-500/30 text-xs md:text-sm font-bold py-2.5 px-4 rounded-2xl shadow-sm hover:shadow-md transition-all hover:scale-105 inline-flex items-center gap-2"
                        onClick={() => setShowTransactionSwiper(true)}
                     >
                        <span className="relative flex h-2 w-2">
                           <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-amber-400 opacity-75"></span>
                           <span className="relative inline-flex rounded-full h-2 w-2 bg-amber-500"></span>
                        </span>
                        <span>Categorize</span>
                        <span className="bg-amber-500/20 dark:bg-amber-500/30 text-amber-800 dark:text-amber-200 text-[11px] font-extrabold px-2 py-0.5 rounded-full border border-amber-500/30">
                           {transactionsToAssign.length} Pending
                        </span>
                     </button>
                  )}
               </div>
            </div>

            {/* Grid Layout for Needs, Wants, Investments */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8 w-full max-w-7xl xl:max-w-[1600px] mb-16 px-2">
               {!loading ? (
                  categoryTypes.map((categoryType) => (
                     <CategoryType
                        key={categoryType.categoryTypeId}
                        categoryType={categoryType}
                        handleShowSplitTransactionModal={handleShowSplitTransactionModal}
                        handleShowReduceTransactionModal={handleShowReduceTransactionModal}
                        handleShowRenameTransactionModal={handleShowRenameTransactionModal}
                        handleShowAssignCategoryModal={handleShowAssignCategoryModal}
                        handleShowUpdateAllocationsModal={handleShowUpdateAllocationsModal}
                        handleShowRenameCategoryModal={handleShowRenameCategoryModal}
                     />
                  ))
               ) : (
                  <div className="col-span-3 flex justify-center py-16">
                     <Loading />
                  </div>
               )}
            </div>
         </div>

         {/* Modals */}
         {showSplitTransactionModal && (
            <SplitTransactionModal
               onClose={handleCloseSplitTransactionModal}
               transaction={transaction}
            />
         )}
         {showReduceTransactionModal && (
            <ReduceTransaction
               onClose={handleCloseReduceTransactionModal}
               transaction={transaction}
            />
         )}
         {showAddTransactionModal && (
            <AddTransaction onClose={handleCloseAddTransactionModal} />
         )}
         {showRenameTransactionModal && (
            <RenameTransaction
               onClose={handleCloseRenameTransactionModal}
               transaction={transaction}
            />
         )}
         {showAssignCategoryModal && (
            <AssignCategoryModal
               onClose={handleCloseAssignCategoryModal}
               transaction={transaction}
            />
         )}
         {showAddCategoryModal && (
            <AddCategory onClose={handleCloseAddCategoryModal} />
         )}
         {showUpdateAllocationsModal && (
            <UpdateAllocationsModal
               onClose={handleCloseUpdateAllocationsModal}
               categoryType={categoryType}
            />
         )}
         {showRenameCategoryModal && (
            <RenameCategory
               onClose={handleCloseRenameCategoryModal}
               category={category}
            />
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
