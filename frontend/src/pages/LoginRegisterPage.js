import React, { useState, useEffect, useContext } from "react";
import { quotes } from "../utils/quotes";
import { Link } from "react-router-dom";

/**
 *  Page to store the functionality to allow user to either login or register
 */
const LoginRegisterPage = () => {
   //Local State
   const [currentQuote, setCurrentQuote] = useState("");

   //Functionality to set a random quote about budgeting
   useEffect(() => {
      const randomIndex = Math.floor(Math.random() * quotes.length);
      setCurrentQuote(quotes[randomIndex]);
   }, []);

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md text-center">
               <h1 className="flex flex-col text-2xl md:text-6xl font-bold mb-8 text-white xs:text-xl">
                  <span className="text-5xl md:text-6xl xs:text-4xl">Bavis</span> 
                  <span className="text-7xl xs:text-5xl">Budgeting</span>
               </h1>
               <p className="text-lg md:text-xl mb-8 text-gray-400 italic">
                  {currentQuote}
               </p>
               <div className="flex flex-col space-y-4">
                  <Link
                     to="/login"
                     className="bg-indigo-600 border-[1px] border-white hover:bg-indigo-700 transition-colors duration-300 text-white font-bold py-2 px-4 rounded mx-auto w-48 md:w-64"
                  >
                     Login
                  </Link>
                  <Link
                     to="/register"
                     className="bg-indigo-600 border-white border-[1px] hover:bg-indigo-700 transition-colors duration-300 text-white font-bold py-2 px-4 rounded mx-auto w-48 md:w-64"
                  >
                     Register
                  </Link>
               </div>
            </div>
         </div>
      </div>
   );
};

export default LoginRegisterPage;
