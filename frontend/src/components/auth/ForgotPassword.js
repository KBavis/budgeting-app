import React from "react";
import { Link } from "react-router-dom";

const ForgotPassword = () => {
   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md w-full px-6 py-8 bg-white rounded-lg shadow-md">
               <h1 className="text-3xl font-bold text-center mb-6 text-indigo-900">
                  Forgot Password
               </h1>
               <p className="text-gray-700 text-center mb-6">
                  Don't worry! Enter your email address below and we'll send you
                  a link to reset your password.
               </p>
               <form className="mb-4">
                  <div className="mb-4">
                     <label
                        className="block text-gray-700 font-bold mb-2"
                        htmlFor="email"
                     >
                        Email Address
                     </label>
                     <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        id="email"
                        type="email"
                        placeholder="Enter your email address"
                     />
                  </div>
                  <div className="flex items-center justify-center">
                     <button
                        className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                        type="submit"
                     >
                        Reset Password
                     </button>
                  </div>
               </form>
               <div className="text-center">
                  <Link
                     to="/login"
                     className="text-gray-700 hover:text-indigo-600"
                  >
                     Back to Login
                  </Link>
               </div>
            </div>
         </div>
      </div>
   );
};

export default ForgotPassword;
