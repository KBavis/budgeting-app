import React, { useContext, useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import authContext from "../../context/auth/authContext";
import AlertContext from "../../context/alert/alertContext";
import Loading from "../util/Loading";

/**
 *  Component utilized for Registering a new User to our application
 */
const Register = () => {
   /**
    * Global States
    */
   const {
      register,
      error,
      isAuthenticated,
      clearErrors,
      loading,
      setLoading,
   } = useContext(authContext);

   const { setAlert } = useContext(AlertContext);

   /**
    * Local States
    */
   const [user, setUser] = useState({
      name: "",
      username: "",
      passwordOne: "",
      passwordTwo: "",
   });

   const { name, username, passwordOne, passwordTwo } = user;

   /**
    * Event Driven Functionality
    */
   const onChange = (e) => {
      setUser({ ...user, [e.target.name]: e.target.value });
   };

   const onSubmit = (e) => {
      e.preventDefault();
      setLoading();
      setTimeout(async () => {
         await register({
            name,
            username,
            passwordOne,
            passwordTwo,
         });
      }, 1000); // 1 second delay
   };

   /**
    * Use Effects
    */
   const navigate = useNavigate();

   // Navigate User To Connect Accounts Or Home Page based on if they have account setup
   useEffect(() => {
      if (isAuthenticated) {
         navigate("/connect-accounts");
      }
   }, [isAuthenticated, navigate]);

   // Handle alerting when errors occur while registering
   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error, clearErrors, setAlert]);

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center font-cool">
         {loading ? (
            <div className="flex justify-center items-center min-h-screen">
               <Loading />
            </div>
         ) : (
            <div className="max-w-md w-full px-6 py-8 bg-white rounded-lg shadow-md relative xs:px-4 xs:py-6">
               <h1 className="text-3xl font-bold text-center mb-6 text-indigo-900 xs:text-2xl">
                  Register
               </h1>
               <form className="flex flex-col" onSubmit={onSubmit}>
                  <div className="absolute top-0 left-0 w-1/6 h-1/6 ml-3 mt-8">
                     <Link to="/">
                        <i className="fa-solid fa-arrow-left-long text-xl hover:cursor-pointer text-indigo-600"></i>
                     </Link>
                  </div>
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
                        name="name"
                        value={name}
                        onChange={onChange}
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
                        name="username"
                        value={username}
                        onChange={onChange}
                        placeholder="Enter your username"
                     />
                  </div>
                  <div className="mb-4">
                     <div className="flex items-center mb-2">
                        <label
                           className="block text-gray-700 font-bold"
                           htmlFor="passwordOne"
                        >
                           Password
                        </label>
                        <div className="relative ml-2 group">
                           <i className="fa-solid fa-circle-info text-indigo-600 hover:text-indigo-500 cursor-pointer duration-75"></i>{" "}
                           <div className="absolute z-10 hidden group-hover:block w-64 md:w-72 right-0 mt-2 px-4 py-2 text-sm bg-white rounded-lg shadow-lg border border-gray-200 whitespace-normal">
                              <p className="mb-2 font-bold">
                                 Password Requirements:
                              </p>
                              <ul className="list-inside text-xs list-none italic">
                                 <li className="mb-1">
                                    - At least one digit (0 - 9)
                                 </li>
                                 <li className="mb-1">
                                    - At least one alphabetical letter (a-zA-Z)
                                 </li>
                                 <li className="mb-1">
                                    - At least one special character
                                    (@#$%^&amp;+=!)
                                 </li>
                                 <li className="mb-1">
                                    - No white space characters
                                 </li>
                                 <li className="mb-1">
                                    - Minimum length of 10 characters
                                 </li>
                              </ul>
                           </div>
                        </div>
                     </div>
                     <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 bg-slate-100 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
                        id="passwordOne"
                        type="password"
                        name="passwordOne"
                        value={passwordOne}
                        onChange={onChange}
                        placeholder="Enter your password"
                     />
                  </div>
                  <div className="mb-6">
                     <label
                        className="block text-gray-700 font-bold mb-2"
                        htmlFor="passwordTwo"
                     >
                        Confirm Password
                     </label>
                     <input
                        className="shadow appearance-none border rounded w-full py-2 px-3 bg-slate-100 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
                        id="passwordTwo"
                        type="password"
                        name="passwordTwo"
                        value={passwordTwo}
                        onChange={onChange}
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
         )}
      </div>
   );
};

export default Register;
