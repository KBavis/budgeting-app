import React, { useState, useEffect } from "react";

const quotes = [
   '"A budget is telling your money where to go instead of wondering where it went." - John Maxwell',
   '"A budget is the ultimate self-care tool." - Gemma Hartley',
   '"Budgeting has only one rule: Do not go over budget." - Leslie Tayne',
   '"A penny saved is a penny earned." - Benjamin Franklin',
   '"Beware of little expenses; a small leak will sink a great ship." - Benjamin Franklin',
   '"Do not tell me wha you value, show me your budget, and I will tell you what your value." - Benjamin Franklin',
   '"The art is not in making money, but in keeping it." - Proverb',
   '"The philosophy behind budgeting is to prioritize your spending and manage your money wisely." - Eleanor Roosevelt',
   '"Budgeting is the process of allocating limited resources across unlimited wants." - John F. Kennedy',
   '"Budgeting is the key to financial freedom." - Amelia Earhart',
];

const LoginRegisterPage = () => {
   const [currentQuote, setCurrentQuote] = useState("");

   useEffect(() => {
      const randomIndex = Math.floor(Math.random() * quotes.length);
      setCurrentQuote(quotes[randomIndex]);
   }, []);

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md text-center">
               <h1 className="text-2xl md:text-6xl font-bold mb-8 text-white">
                  Bavis <span className="text-7xl">Budgeting</span>
               </h1>
               <p className="text-lg md:text-xl mb-8 text-gray-400 italic">
                  {currentQuote}
               </p>
               <div className="flex flex-col space-y-4">
                  <button className="bg-gradient-to-r from-indigo-600 to-gray-800 hover:from-indigo-700 hover:to-gray-700 text-white font-bold py-2 px-4 rounded mx-auto w-48 md:w-64">
                     Login
                  </button>
                  <button className="bg-gradient-to-r from-gray-800 to-indigo-600 hover:from-gray-700 hover:to-indigo-700 text-white font-bold py-2 px-4 rounded mx-auto w-48 md:w-64">
                     Register
                  </button>
               </div>
            </div>
         </div>
      </div>
   );
};

export default LoginRegisterPage;
