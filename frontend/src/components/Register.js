import React from "react";

const Register = () => {
   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center font-cool">
         <div className="max-w-md w-full px-6 py-8 bg-white rounded-lg shadow-md">
            <h1 className="text-3xl font-bold text-center mb-6 text-indigo-900">
               Register
            </h1>
            <form className="flex flex-col">
               <div className="mb-4">
                  <label
                     className="block text-gray-700 font-bold mb-2"
                     htmlFor="name"
                  >
                     Name
                  </label>
                  <input
                     className="shadow appearance-none border rounded w-full bg-slate-100 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                     id="name"
                     type="text"
                     placeholder="Enter your name"
                  />
               </div>
               <div className="mb-4">
                  <label
                     className="block text-gray-700 font-bold mb-2"
                     htmlFor="username"
                  >
                     Username
                  </label>
                  <input
                     className="shadow appearance-none border rounded w-full py-2 px-3 bg-slate-100 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                     id="username"
                     type="text"
                     placeholder="Enter your username"
                  />
               </div>
               <div className="mb-4">
                  <label
                     className="block text-gray-700 font-bold mb-2"
                     htmlFor="password"
                  >
                     Password
                  </label>
                  <input
                     className="shadow appearance-none border rounded w-full py-2 px-3 bg-slate-100 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
                     id="password"
                     type="password"
                     placeholder="Enter your password"
                  />
               </div>
               <div className="mb-6">
                  <label
                     className="block text-gray-700 font-bold mb-2"
                     htmlFor="confirmPassword"
                  >
                     Confirm Password
                  </label>
                  <input
                     className="shadow appearance-none border rounded w-full py-2 px-3 bg-slate-100 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
                     id="confirmPassword"
                     type="password"
                     placeholder="Confirm your password"
                  />
               </div>
               <div className="flex justify-center">
                  <button
                     className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                     type="submit"
                  >
                     Register
                  </button>
               </div>
            </form>
         </div>
      </div>
   );
};

export default Register;
