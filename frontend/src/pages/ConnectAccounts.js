import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PlaidLink } from "react-plaid-link";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";
import AlertContext from "../context/alert/alertContext";
import { FaCheckCircle, FaUniversity, FaArrowRight } from "react-icons/fa";

import StepProgress from "../components/layout/StepProgress";

/**
 * Component to connect to external financial institutions via Plaid API
 */
const ConnectAccounts = () => {
   // Global State
   const navigate = useNavigate();
   const { user, fetchAuthenticatedUser, refreshLinkToken } = useContext(authContext);
   const { createAccount, accounts, fetchAccounts } = useContext(accountContext);
   const { setAlert } = useContext(AlertContext);
   const [connectedAccounts, setConnectedAccounts] = useState([]);

   // Map account type to enum
   const mapAccountType = (type, subtype) => {
      switch (type) {
         case "depository":
            return subtype === "checking" ? "CHECKING" : "SAVING";
         case "credit":
            return "CREDIT";
         case "loan":
            return "LOAN";
         case "investment":
            return "INVESTMENT";
         default:
            return null;
      }
   };

   // Handle successful connection via Plaid
   const handleOnSuccess = (publicToken, metadata) => {
      const accountsList = (metadata.accounts && metadata.accounts.length > 0)
         ? metadata.accounts
         : [metadata.account];

      accountsList.forEach((acc) => {
         const accountData = {
            plaidAccountId: acc.id || metadata.account_id,
            accountName: `${metadata.institution.name} - ${acc.name}`,
            publicToken,
            accountType: mapAccountType(
               acc.type || metadata.account.type,
               acc.subtype || metadata.account.subtype
            ) || "CHECKING",
         };

         createAccount(accountData);
         setConnectedAccounts((prev) => [...prev, accountData]);
      });

      setAlert(`Successfully connected ${metadata.institution.name}!`, "SUCCESS");
   };

   const handleOnExit = (err, metadata) => {
      if (err) {
         console.log("Plaid Link Exit Error:", err);
      }
   };

   const handleOnContinue = () => {
      navigate("/income");
   };

   useEffect(() => {
      if (!user && localStorage.token) {
         fetchAuthenticatedUser();
      }
   }, []);

   useEffect(() => {
      if (user && (!user.linkToken || (user.linkToken.expiration && new Date() > new Date(user.linkToken.expiration)))) {
         refreshLinkToken();
      }
   }, [user, refreshLinkToken]);

   const hasConnected = connectedAccounts.length > 0 || (accounts && accounts.length > 0);

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center px-4 py-8">
         {/* Progress Bar */}
         <StepProgress currentStep={2} totalSteps={5} />

         {/* Main Card */}
         <div className="max-w-md w-full bg-slate-900/80 backdrop-blur-md border border-slate-700/80 rounded-2xl shadow-2xl p-8 animate-slide-up text-center xs:p-6">
            <div className="inline-flex p-3 bg-brand-500/20 text-brand-400 rounded-2xl border border-brand-500/30 mb-4">
               <FaUniversity className="w-8 h-8" />
            </div>

            <h1 className="text-3xl font-extrabold text-white mb-2 xs:text-2xl">
               Connect Your Bank
            </h1>
            <p className="text-sm text-slate-400 mb-6">
               Securely sync transactions & balances automatically using Plaid.
            </p>

            {/* Connected Accounts List */}
            {hasConnected && (
               <div className="mb-6 flex flex-col gap-2 text-left">
                  <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1">
                     Connected Accounts
                  </p>
                  {(accounts && accounts.length > 0 ? accounts : connectedAccounts).map((acc, idx) => (
                     <div
                        key={acc.plaidAccountId || idx}
                        className="flex items-center justify-between p-3 bg-slate-800/80 border border-slate-700/60 rounded-xl animate-fade-in"
                     >
                        <div className="flex items-center gap-3">
                           <FaCheckCircle className="text-emerald-400 w-4 h-4 flex-shrink-0" />
                           <span className="text-sm font-semibold text-slate-100 truncate">
                              {acc.accountName}
                           </span>
                        </div>
                        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-brand-500/20 text-brand-300 border border-brand-500/30 uppercase">
                           {acc.accountType}
                        </span>
                     </div>
                  ))}
               </div>
            )}

            {/* Plaid Link Button */}
            <PlaidLink
               token={user?.linkToken?.token}
               onSuccess={handleOnSuccess}
               onExit={handleOnExit}
               className="plaid-link-wrapper-class w-full block"
               style={{ background: "none", border: "none", padding: 0 }}
            >
               <button
                  type="button"
                  className="w-full bg-brand-600 hover:bg-brand-500 text-white font-bold py-3 px-6 rounded-xl transition-all duration-300 shadow-lg hover:shadow-brand-500/25 hover:scale-[1.02] flex items-center justify-center gap-2"
               >
                  <span>{hasConnected ? "Add Another Account" : "Link Account via Plaid"}</span>
               </button>
            </PlaidLink>

            {/* Action Buttons */}
            <div className="mt-6 flex flex-col gap-3 pt-6 border-t border-slate-800">
               {hasConnected ? (
                  <button
                     onClick={handleOnContinue}
                     className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-bold py-3 px-6 rounded-xl transition-all duration-300 shadow-lg flex items-center justify-center gap-2"
                  >
                     <span>Continue to Next Step</span>
                     <FaArrowRight className="w-4 h-4" />
                  </button>
               ) : (
                  <button
                     onClick={handleOnContinue}
                     className="text-xs font-semibold text-slate-400 hover:text-slate-200 transition-colors py-2"
                  >
                     Skip for now (you can connect accounts later)
                  </button>
               )}
            </div>
         </div>
      </div>
   );
};

export default ConnectAccounts;
