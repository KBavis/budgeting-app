import React, { useContext, useState, useEffect } from "react";
import { Link } from "react-router-dom";
import authContext from "../../context/auth/authContext";
import { useNavigate } from "react-router-dom";
import AlertContext from "../../context/alert/alertContext";

const Register = () => {
   /**
    * Global States
    */
   const { register, error, isAuthenticated, clearErrors } =
      useContext(authContext);

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
      console.log(user);
      register({
         name,
         username,
         passwordOne,
         passwordTwo,
      });
   };

   /**
    * Use Efects
    */
   const navigate = useNavigate();

   //TOOD: Update this to either navigate to connect accounts page or home page based on if user has connections established
   useEffect(() => {
      if (isAuthenticated) {
         navigate("/connect-accounts");
      }
   }, [isAuthenticated]);

   useEffect(() => {
      if (error) {
         setAlert(error, "danger");
         clearErrors();
      }
   }, [error]);

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center font-cool">
         <div className="max-w-md w-full px-6 py-8 bg-white rounded-lg shadow-md relative">
            <h1 className="text-3xl font-bold text-center mb-6 text-indigo-900">
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
                  <label
                     className="block text-gray-700 font-bold mb-2"
                     htmlFor="password1"
                  >
                     Password
                  </label>
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
                     id="password2"
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
      </div>
   );
};

export default Register;
