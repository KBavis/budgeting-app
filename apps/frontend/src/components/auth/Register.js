import React, { useContext, useState, useEffect, useMemo } from "react";
import { Link, useNavigate } from "react-router-dom";
import authContext from "../../context/auth/authContext";
import AlertContext from "../../context/alert/alertContext";
import Loading from "../util/Loading";
import StepProgress from "../layout/StepProgress";

/**
 * Password strength evaluator
 * Returns { score: 0-4, label, color } based on length, digits, specials, mixed case
 */
const evaluatePasswordStrength = (password) => {
   if (!password) return { score: 0, label: "", color: "bg-slate-700" };

   let score = 0;
   if (password.length >= 10) score++;
   if (/[0-9]/.test(password)) score++;
   if (/[a-zA-Z]/.test(password)) score++;
   if (/[@#$%^&+=!]/.test(password)) score++;

   const levels = [
      { score: 0, label: "", color: "bg-slate-700" },
      { score: 1, label: "Weak", color: "bg-red-500" },
      { score: 2, label: "Fair", color: "bg-amber-500" },
      { score: 3, label: "Good", color: "bg-emerald-400" },
      { score: 4, label: "Strong", color: "bg-emerald-500" },
   ];

   return levels[score] || levels[0];
};

/**
 * Component utilized for Registering a new User to our application
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

   const passwordStrength = useMemo(
      () => evaluatePasswordStrength(passwordOne),
      [passwordOne]
   );

   const passwordsMatch = passwordTwo.length > 0 && passwordOne === passwordTwo;
   const passwordsMismatch = passwordTwo.length > 0 && passwordOne !== passwordTwo;

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
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center px-4">
         {loading ? (
            <div className="flex justify-center items-center min-h-screen">
               <Loading />
            </div>
         ) : (
            <>
               {/* Step Progress */}
               <StepProgress currentStep={1} totalSteps={5} />

               {/* Registration Card */}
               <div className="max-w-md w-full bg-slate-900/80 backdrop-blur-md border border-slate-700/80 rounded-2xl shadow-2xl p-8 animate-slide-up xs:p-6">
                  {/* Header */}
                  <div className="text-center mb-6">
                     <h1 className="text-3xl font-extrabold text-white mb-1 xs:text-2xl">
                        Create Your Account
                     </h1>
                     <p className="text-sm text-slate-400">
                        Start your budgeting journey
                     </p>
                  </div>

                  <form className="flex flex-col gap-4" onSubmit={onSubmit}>
                     {/* Back Arrow */}
                     <Link
                        to="/"
                        className="absolute top-4 left-4 p-2 text-slate-400 hover:text-white rounded-lg transition-colors"
                     >
                        <i className="fa-solid fa-arrow-left-long text-xl"></i>
                     </Link>

                     {/* Full Name */}
                     <div className="flex flex-col gap-1">
                        <label
                           className="text-xs font-semibold text-slate-300"
                           htmlFor="name"
                        >
                           Full Name
                        </label>
                        <input
                           className="bg-slate-800 border border-slate-700 text-slate-100 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:border-brand-500 transition-colors placeholder-slate-500"
                           id="name"
                           type="text"
                           name="name"
                           value={name}
                           onChange={onChange}
                           placeholder="Enter your full name"
                        />
                     </div>

                     {/* Username */}
                     <div className="flex flex-col gap-1">
                        <label
                           className="text-xs font-semibold text-slate-300"
                           htmlFor="username"
                        >
                           Username
                        </label>
                        <input
                           className="bg-slate-800 border border-slate-700 text-slate-100 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:border-brand-500 transition-colors placeholder-slate-500"
                           id="username"
                           type="text"
                           name="username"
                           value={username}
                           onChange={onChange}
                           placeholder="Choose a username"
                        />
                     </div>

                     {/* Password */}
                     <div className="flex flex-col gap-1">
                        <div className="flex items-center gap-2 mb-0.5">
                           <label
                              className="text-xs font-semibold text-slate-300"
                              htmlFor="passwordOne"
                           >
                              Password
                           </label>
                           <div className="relative group">
                              <i className="fa-solid fa-circle-info text-brand-400 hover:text-brand-300 cursor-pointer text-xs"></i>
                              <div className="absolute z-10 hidden group-hover:block w-64 right-0 mt-2 px-4 py-3 text-sm bg-slate-800 rounded-xl shadow-xl border border-slate-700 whitespace-normal">
                                 <p className="mb-2 font-bold text-slate-200 text-xs">
                                    Password Requirements:
                                 </p>
                                 <ul className="text-xs text-slate-400 space-y-1">
                                    <li className={/[0-9]/.test(passwordOne) ? "text-emerald-400" : ""}>
                                       • At least one digit (0–9)
                                    </li>
                                    <li className={/[a-zA-Z]/.test(passwordOne) ? "text-emerald-400" : ""}>
                                       • At least one letter (a–z, A–Z)
                                    </li>
                                    <li className={/[@#$%^&+=!]/.test(passwordOne) ? "text-emerald-400" : ""}>
                                       • At least one special character (@#$%^&+=!)
                                    </li>
                                    <li className={!/\s/.test(passwordOne) && passwordOne.length > 0 ? "text-emerald-400" : ""}>
                                       • No white space characters
                                    </li>
                                    <li className={passwordOne.length >= 10 ? "text-emerald-400" : ""}>
                                       • Minimum 10 characters
                                    </li>
                                 </ul>
                              </div>
                           </div>
                        </div>
                        <input
                           className="bg-slate-800 border border-slate-700 text-slate-100 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:border-brand-500 transition-colors placeholder-slate-500"
                           id="passwordOne"
                           type="password"
                           name="passwordOne"
                           value={passwordOne}
                           onChange={onChange}
                           placeholder="Create a password"
                        />
                        {/* Password Strength Indicator */}
                        {passwordOne.length > 0 && (
                           <div className="flex items-center gap-2 mt-1.5 animate-fade-in">
                              <div className="flex gap-1 flex-1">
                                 {[1, 2, 3, 4].map((level) => (
                                    <div
                                       key={level}
                                       className={`h-1 flex-1 rounded-full transition-all duration-300 ${
                                          level <= passwordStrength.score
                                             ? passwordStrength.color
                                             : "bg-slate-700"
                                       }`}
                                    />
                                 ))}
                              </div>
                              <span
                                 className={`text-[10px] font-semibold uppercase tracking-wider ${
                                    passwordStrength.score <= 1
                                       ? "text-red-400"
                                       : passwordStrength.score === 2
                                       ? "text-amber-400"
                                       : "text-emerald-400"
                                 }`}
                              >
                                 {passwordStrength.label}
                              </span>
                           </div>
                        )}
                     </div>

                     {/* Confirm Password */}
                     <div className="flex flex-col gap-1">
                        <label
                           className="text-xs font-semibold text-slate-300"
                           htmlFor="passwordTwo"
                        >
                           Confirm Password
                        </label>
                        <input
                           className={`bg-slate-800 border text-slate-100 rounded-lg px-3 py-2.5 text-sm focus:outline-none transition-colors placeholder-slate-500 ${
                              passwordsMismatch
                                 ? "border-red-500/60 focus:border-red-500"
                                 : passwordsMatch
                                 ? "border-emerald-500/60 focus:border-emerald-500"
                                 : "border-slate-700 focus:border-brand-500"
                           }`}
                           id="passwordTwo"
                           type="password"
                           name="passwordTwo"
                           value={passwordTwo}
                           onChange={onChange}
                           placeholder="Confirm your password"
                        />
                        {passwordsMismatch && (
                           <p className="text-[11px] text-red-400 font-medium mt-0.5 animate-fade-in">
                              Passwords do not match
                           </p>
                        )}
                        {passwordsMatch && (
                           <p className="text-[11px] text-emerald-400 font-medium mt-0.5 animate-fade-in">
                              ✓ Passwords match
                           </p>
                        )}
                     </div>

                     {/* Submit */}
                     <button
                        className="w-full mt-2 bg-brand-600 hover:bg-brand-500 text-white font-bold py-2.5 px-4 rounded-xl transition-all duration-300 shadow-lg hover:shadow-brand-500/25 hover:scale-[1.02] disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
                        type="submit"
                        disabled={!name || !username || !passwordOne || !passwordTwo || passwordsMismatch}
                     >
                        Create Account
                     </button>

                     {/* Login link */}
                     <p className="text-center text-sm text-slate-400 mt-2">
                        Already have an account?{" "}
                        <Link
                           to="/login"
                           className="text-brand-400 hover:text-brand-300 font-semibold transition-colors"
                        >
                           Sign In
                        </Link>
                     </p>
                  </form>
               </div>
            </>
         )}
      </div>
   );
};

export default Register;
