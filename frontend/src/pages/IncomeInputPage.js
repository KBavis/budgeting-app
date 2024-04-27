import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AlertContext from "../context/alert/alertContext";
import IncomeContext from "../context/income/incomeContext";

const IncomeInputPage = () => {
   const navigate = useNavigate();
   const { setAlert } = useContext(AlertContext);
   const { error, addIncome, clearErrors, incomes } = useContext(IncomeContext);
   const [income, setIncome] = useState("");
   const [incomeSource, setIncomeSource] = useState("");
   const [incomeType, setIncomeType] = useState("");
   const [description, setDescription] = useState("");

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

   useEffect(() => {
      if (incomes && incomes.length > 0) {
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

   const handleSubmit = (e) => {
      e.preventDefault();
      const capitalizedSource = incomeSource.toUpperCase().replace(/\s/g, "_");
      const capitalizedType = incomeType.toUpperCase().replace(/\s/g, "_");
      const formData = {
         amount: parseFloat(income),
         incomeSource: capitalizedSource,
         incomeType: capitalizedType,
         description: description,
      };
      console.log(formData);
      addIncome(formData);
   };

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center">
         <div className="max-w-md text-center">
            <h1 className="text-4xl font-bold mb-8 text-white">
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
                     className="w-full pl-7 pr-4 py-3 rounded-lg bg-gray-800 text-white focus:outline-none focus:ring-2 focus:ring-indigo-600 border-2 border-indigo-600 appearance-none"
                     placeholder="Amount"
                  />
               </div>
               <div className="mb-6">
                  <input
                     id="description"
                     value={description}
                     onChange={(e) => setDescription(e.target.value)}
                     className="w-full px-4 py-3 rounded-lg bg-gray-800 text-white focus:outline-none focus:ring-2 focus:ring-indigo-600 appearance-none"
                     placeholder="Description"
                  />
               </div>
               <div className="mb-6">
                  <div className="relative">
                     <select
                        id="incomeSource"
                        value={incomeSource}
                        onChange={(e) => setIncomeSource(e.target.value)}
                        className="w-full px-4 py-3 pr-8 rounded-lg bg-gray-800 text-gray-400 text-left focus:outline-none focus:ring-2 focus:ring-gray-600 appearance-none"
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
                        className="w-full px-4 py-3 pr-8 rounded-lg bg-gray-800 text-gray-400 text-left focus:outline-none focus:ring-2 focus:ring-gray-600 appearance-none"
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
