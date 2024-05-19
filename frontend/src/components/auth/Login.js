import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import authContext from "../../context/auth/authContext";
import React from "react";
import { Link } from "react-router-dom";
import AlertContext from "../../context/alert/alertContext";

/**
 *  Component to allow user to authenticate with their account
 */
const Login = () => {
   const navigate = useNavigate();
   /**
    * Global States
    */
   const { login, error, isAuthenticated, clearErrors, user } =
      useContext(authContext);

   const { setAlert } = useContext(AlertContext);

   /**
    * Local States
    */
   const [username, setUsername] = useState("");
   const [passwordOne, setPasswordOne] = useState("");

   /**
    * Event Driven Functionality
    */
   const onChange = (e) => {
      if (e.target.name === "username") {
         setUsername(e.target.value);
      } else if (e.target.name === "passwordOne") {
         setPasswordOne(e.target.value);
      }
   };

   const onSubmit = (e) => {
      e.preventDefault();
      console.log({ username, passwordOne });
      login({ username, passwordOne });
   };

   /**
    * Use Efects
    */

   //Navigate User To Connect Accounts Or Home Page depending on if they've already connected accounts
   useEffect(() => {
      if (isAuthenticated) {
         if (user && user.accounts) {
            navigate("/home");
         } else {
            navigate("/connect-accounts");
         }
      }
   }, [isAuthenticated, user]);

   //Utilize Alert Context To Notify User of Unsuccesfull/Succesful Authentication
   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error]);

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
         <div className="flex-1 flex flex-col justify-center items-center px-8 md:px-12">
            <div className="max-w-md w-full px-6 py-8 bg-white rounded-lg shadow-md relative">
               <h1 className="text-3xl font-bold text-center mb-6 text-indigo-900">
                  Login
               </h1>
               <form onSubmit={onSubmit}>
                  <div className="absolute top-0 left-0 w-1/6 h-1/6 ml-3 mt-8">
                     <Link to="/">
                        <i className="fa-solid fa-arrow-left-long text-xl hover:cursor-pointer text-indigo-600"></i>
                     </Link>
                  </div>
                  <div className="mb-4">
                     <label
                        className="block text-gray-700 font-bold mb-2"
                        htmlFor="username"
                     >
                        Username
                     </label>
                     <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline bg-slate-100"
                        id="username"
                        name="username"
                        type="text"
                        placeholder="Enter your username"
                        onChange={onChange}
                     />
                  </div>
                  <div className="mb-6">
                     <label
                        className="block text-gray-700 font-bold mb-2"
                        htmlFor="password"
                     >
                        Password
                     </label>
                     <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline bg-slate-100"
                        id="passwordOne"
                        type="password"
                        name="passwordOne"
                        placeholder="Enter your password"
                        onChange={onChange}
                     />
                  </div>
                  <div className="flex items-center justify-center mb-4">
                     <button
                        className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                        type="submit"
                     >
                        Login
                     </button>
                  </div>
                  <div className="text-center">
                     <Link
                        to="/forgot-password"
                        className="text-gray-700 hover:text-indigo-600"
                     >
                        Forgot Password?
                     </Link>
                  </div>
               </form>
            </div>
         </div>
      </div>
   );
};

export default Login;
