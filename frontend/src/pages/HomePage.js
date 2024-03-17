// src/components/HomePage.js
import React from "react";

const HomePage = () => {
   return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
         <div className="max-w-md w-full px-6 py-8 bg-white rounded-lg shadow-md">
            <h1 className="text-3xl font-bold text-center mb-6">
               Welcome to Bavis Budgeting
            </h1>
            <p className="text-gray-600 text-center mb-8">
               "Budgeting has only one rule: Do not go over budget." - Leslie
               Tayne
            </p>
            <div className="flex justify-center">
               <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                  Connect Financial Institution
               </button>
            </div>
         </div>
      </div>
   );
};

export default HomePage;
