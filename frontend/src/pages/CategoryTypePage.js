import React, { useContext, useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import categoryContext from "../context/category/categoryContext";
import DetailedCategory from "../components/category/DetailedCategory";
import { FaArrowLeft } from "react-icons/fa";
import authContext from "../context/auth/authContext";
import transactionContext from "../context/transaction/transactionContext";
import SplitTransactionModal from "../components/transaction/SplitTransaction";
import ReduceTransaction from "../components/transaction/ReduceTransaction";
import RenameTransaction from "../components/transaction/RenameTransaction";
import AssignCategoryModal from "../components/transaction/AssignCategoryModal";
import UpdateAllocationsModal from "../components/category/UpdateAllocationsModal";
import RenameCategory from "../components/category/RenameCategory";

/**
 * CategoryType Page for all corresponding Categories
 * associated with CategoryType
 *
 * @param categoryType
 *          - CategoryType to generate page for
 */
const CategoryTypePage = ({ categoryType }) => {
   const { categoryTypes, fetchCategoryTypes } =
       useContext(categoryTypeContext);
   const { categories, fetchCategories } = useContext(categoryContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { transactions, fetchTransactions } = useContext(transactionContext);
   const [filteredCategories, setFilteredCategories] = useState([]);
   const [filterQuery, setFilterQuery] = useState("");
   const [showSplitTransactionModal, setShowSplitTransactionModal] =
       useState(false);
   const [selectedCategoryType, setSelectedCategoryType] = useState(false);
   const [showReduceTransactionModal, setShowReduceTransactionModal] =
       useState(false);
   const [showRenameTransactionModal, setShowRenameTransactionModal] =
       useState(false);
   const [showAssignCategoryModal, setShowAssignCategoryModal] =
       useState(false);
   const [showUpdateAllocationsModal, setShowUpdateAllocationsModal] =
       useState(false);
   const [showRenameCategoryModal, setShowRenameCategoryModal] =
       useState(false);
   const [currentTransaction, setCurrentTransaction] = useState(null);
   const [selectedCategory, setSelectedCategory] =
       useState(null);
   const navigate = useNavigate();

   const initialFetchRef = useRef(false);

   //Filter The Categories for Current Category Type
   useEffect(() => {
      const currentCategoryType = categoryTypes.find(
          (ct) => ct.name.toLowerCase() === categoryType.toLowerCase()
      );

      if (currentCategoryType) {
         const filtered = categories.filter(
             (category) =>
                 category.categoryType.categoryTypeId ===
                 currentCategoryType.categoryTypeId
         );
         setFilteredCategories(filtered);
      }
   }, [categoryTypes, categories, categoryType]);

   //Fetch All Values On Refresh
   useEffect(() => {
      const fetch = async () => {
         if (!user && localStorage.token) {
            await fetchAuthenticatedUser();
         }

         if (!categoryTypes || categoryTypes.length === 0) {
            await fetchCategoryTypes();
         }

         if (!categories || categories.length === 0) {
            await fetchCategories();
         }

         if (!transactions || transactions.length === 0) {
            await fetchTransactions();
         }
      };

      if (!initialFetchRef.current) {
         fetch();
         initialFetchRef.current = true;
      }
   }, [
      user,
      fetchAuthenticatedUser,
      categoryTypes,
      fetchCategoryTypes,
      categories,
      fetchCategories,
      transactions,
      fetchTransactions,
   ]);

   const handleBackClick = () => {
      navigate("/home");
   };

   // Modal handlers
   const handleShowSplitTransactionModal = (transaction) => {
      setShowSplitTransactionModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseSplitTransactionModal = () => {
      setShowSplitTransactionModal(false);
   };

   const handleShowReduceTransactionModal = (transaction) => {
      setShowReduceTransactionModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseReduceTransactionModal = () => {
      setShowReduceTransactionModal(false);
   };

   const handleShowRenameTransactionModal = (transaction) => {
      setShowRenameTransactionModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseRenameTransactionModal = () => {
      setShowRenameTransactionModal(false);
   };

   const handleShowAssignCategoryModal = (transaction) => {
      setShowAssignCategoryModal(true);
      setCurrentTransaction(transaction);
   };

   const handleCloseAssignCategoryModal = () => {
      setShowAssignCategoryModal(false);
   };

   //Function to open AdjustCategoryAllocation modal
   const handleShowUpdateAllocationsModal = (type) => {
      setSelectedCategoryType(type);
      setShowUpdateAllocationsModal(true);
   };

   //Function to close AdjustCategoryAllocation modal
   const handleCloseUpdateAllocationsModal = () => {
      setShowUpdateAllocationsModal(false);
   };

   //Function to open RenameCategory modal
   const handleShowRenameCategoryModal = (category) => {
      setSelectedCategory(category);
      setShowRenameCategoryModal(true);
   }

   //Function to close RenameCategory modal
   const handleCloseRenameCategoryModal = () => {
      setShowRenameCategoryModal(false);
   }

   // Filter categories based on the filter query
   const filteredCategoriesByQuery = filteredCategories.filter((category) =>
       category.name.toLowerCase().startsWith(filterQuery.toLowerCase())
   );

   return (
       <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
          <FaArrowLeft
              className="text-3xl text-white ml-5 mt-5 hover:scale-105 hover:text-gray-200 cursor-pointer z-[500] xs:text-2xl xs:ml-3 xs:mt-3"
              onClick={handleBackClick}
          />
          <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
             <div className="max-w-full md:max-w-md text-center mb-8 xs:mb-4">
                <h2 className="text-4xl md:text-5xl font-bold text-white xs:text-3xl">
                   Explore your{" "}
                   <span className="text-indigo-600"> {categoryType} </span>
                </h2>
                <input
                    type="text"
                    placeholder="Filter categories..."
                    value={filterQuery}
                    onChange={(e) => setFilterQuery(e.target.value)}
                    className="mt-4 p-2 w-full rounded-md text-black xs:mt-2 xs:p-1 xs:text-sm"
                />
             </div>
             <div
                 className="flex flex-wrap justify-center items-center w-full space-y-8 overflow-y-auto scrollbar-hide h-screen pb-20">
                {filteredCategoriesByQuery.length > 0 ? (
                    filteredCategoriesByQuery.map((category) => (
                        <DetailedCategory
                            key={category.categoryId}
                            category={category}
                            handleShowSplitTransactionModal={handleShowSplitTransactionModal}
                            handleShowReduceTransactionModal={handleShowReduceTransactionModal}
                            handleShowRenameTransactionModal={handleShowRenameTransactionModal}
                            handleShowAssignCategoryModal={handleShowAssignCategoryModal}
                            handleShowUpdateAllocationsModal={handleShowUpdateAllocationsModal}
                            handleShowRenameCategoryModal={handleShowRenameCategoryModal}
                        />
                    ))
                ) : (
                    <div className="flex flex-col items-center justify-center w-full py-8 text-center">
                       <h2 className="text-2xl md:text-3xl font-bold text-white p-4 rounded-md shadow-md border-white border">
                          No categories found
                          <p className="text-lg text-gray-300 mt-4">Try adjusting your filters or add new
                             categories.</p>
                       </h2>
                    </div>
                )}
             </div>
          </div>
          {/* Modals */}
          {showSplitTransactionModal && (
              <div
                  className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                 <SplitTransactionModal
                     onClose={handleCloseSplitTransactionModal}
                     transaction={currentTransaction}
                 />
              </div>
          )}
          {showReduceTransactionModal && (
              <div
                  className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                 <ReduceTransaction
                     onClose={handleCloseReduceTransactionModal}
                     transaction={currentTransaction}
                 />
              </div>
          )}
          {showRenameTransactionModal && (
              <div
                  className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                 <RenameTransaction
                     onClose={handleCloseRenameTransactionModal}
                     transaction={currentTransaction}
                 />
              </div>
          )}
          {showAssignCategoryModal && (
              <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                 <AssignCategoryModal
                     onClose={handleCloseAssignCategoryModal}
                     transaction={currentTransaction}
                 />
              </div>
          )}
          {showUpdateAllocationsModal && (
              <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                 <UpdateAllocationsModal
                     onClose={handleCloseUpdateAllocationsModal}
                     categoryType={selectedCategoryType}
                 />
              </div>
          )}
          {showRenameCategoryModal && (
              <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                 <RenameCategory
                     onClose={handleCloseRenameCategoryModal}
                     category={selectedCategory}
                 />
              </div>
          )}
       </div>
   );
};

export default CategoryTypePage;
