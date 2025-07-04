import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AlertContext from "../context/alert/alertContext";
import IncomeContext from "../context/income/incomeContext";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";

/**
 *  Page for users to enter their monthly income
 */
const IncomeInputPage = () => {
   //Local States
   const [income, setIncome] = useState("");
   const [incomeSource, setIncomeSource] = useState("");
   const [incomeType, setIncomeType] = useState("");
   const [description, setDescription] = useState("");

   const navigate = useNavigate();
   //Global States
   const { setAlert } = useContext(AlertContext);
   const { error, addIncome, clearErrors, incomes } = useContext(IncomeContext);
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { accounts, fetchAccounts } = useContext(accountContext);

   //List of potential income sources
   const incomeSources = [
      "Employer",
      "Client",
      "Property",
      "Stock",
      "Mutual Fund",
      "Bond",
      "Savings Account",
      "Retirement Account",
      "Government",
      "Family",
      "Friend",
      "Business",
      "Intellectual Property",
      "Other",
   ];

   //List of potential income types
   const incomeTypes = [
      "Salary",
      "Bonus",
      "Commission",
      "Freelance",
      "Rental",
      "Investment",
      "Pension",
      "Social Security",
      "Alimony",
      "Child Support",
      "Royalties",
      "Capital Gains",
      "Gift",
      "Other",
   ];

   //Alert use and navigate in the successful case
   useEffect(() => {
      if (incomes) {
         setAlert("Income added successfully", "SUCCESS");
         navigate("/category-types");
      }
   }, [incomes]);

   //Utilize Alert Context To Notify User of Unsuccesfull Authentication
   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error]);

   //Functionality to submit users selected incomes
   const handleSubmit = (e) => {
      e.preventDefault();
      const capitalizedSource = incomeSource.toUpperCase().replace(/\s/g, "_");
      const capitalizedType = incomeType.toUpperCase().replace(/\s/g, "_");
      //Generate payload to send to API
      const formData = {
         amount: parseFloat(income),
         incomeSource: capitalizedSource,
         incomeType: capitalizedType,
         description: description,
      };
      addIncome(formData);
   };

   //Fetch Needed Information on Refresh
   useEffect(() => {
      if (!user && localStorage.token) {
         fetchAuthenticatedUser();
      }

      if (!accounts) {
         fetchAccounts();
      }
   }, []);

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center">
         <div className="max-w-md text-center">
            <h1 className="text-4xl font-bold mb-8 text-white xs:text-3xl">
               Enter Your Monthly Income
            </h1>
            <form onSubmit={handleSubmit}>
               <div className="mb-6 relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                     <span className="text-gray-400 sm:text-sm">$</span>
                  </div>
                  <input
                     id="income"
                     value={income}
                     onChange={(e) => setIncome(e.target.value)}
                     className="w-full pl-7 pr-4 py-3 rounded-lg border-[1px] border-white bg-gray-800 text-white focus:outline-none focus:bg-gray-800"
                     placeholder="Amount"
                  />
               </div>
               <div className="mb-6">
                  <input
                     id="description"
                     value={description}
                     onChange={(e) => setDescription(e.target.value)}
                     className="w-full px-4 py-3 rounded-lg bg-gray-800 border-[1px] border-white text-white focus:outline-none focus:bg-gray-800"
                     placeholder="Description"
                  />
               </div>
               <div className="mb-6">
                  <div className="relative">
                     <select
                        id="incomeSource"
                        value={incomeSource}
                        onChange={(e) => setIncomeSource(e.target.value)}
                        className="w-full px-4 py-3 pr-8 rounded-lg bg-gray-800 border-[1px] border-white text-gray-400 text-left focus:outline-none focus:bg-gray-800 appearance-none"
                     >
                        <option value="" disabled hidden>
                           Select Income Source
                        </option>
                        {incomeSources.map((source) => (
                           <option key={source} value={source}>
                              {source}
                           </option>
                        ))}
                     </select>
                     <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                        <svg
                           className="w-4 h-4 text-white"
                           fill="none"
                           stroke="currentColor"
                           viewBox="0 0 24 24"
                           xmlns="http://www.w3.org/2000/svg"
                        >
                           <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M19 9l-7 7-7-7"
                           />
                        </svg>
                     </div>
                  </div>
               </div>
               <div className="mb-6">
                  <div className="relative">
                     <select
                        id="incomeType"
                        value={incomeType}
                        onChange={(e) => setIncomeType(e.target.value)}
                        className="w-full px-4 py-3 pr-8 rounded-lg bg-gray-800 text-gray-400 border-[1px] border-white text-left focus:outline-none focus:bg-gray-800 appearance-none"
                     >
                        <option value="" disabled hidden>
                           Select Income Type
                        </option>
                        {incomeTypes.map((type) => (
                           <option key={type} value={type}>
                              {type}
                           </option>
                        ))}
                     </select>
                     <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                        <svg
                           className="w-4 h-4 text-white"
                           fill="none"
                           stroke="currentColor"
                           viewBox="0 0 24 24"
                           xmlns="http://www.w3.org/2000/svg"
                        >
                           <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M19 9l-7 7-7-7"
                           />
                        </svg>
                     </div>
                  </div>
               </div>
               <button
                  type="submit"
                  className="font-bold py-3 px-6 rounded text-white bg-indigo-600 hover:bg-indigo-700 transition-colors duration-300"
               >
                  Submit
               </button>
            </form>
         </div>
      </div>
   );
};

export default IncomeInputPage;
