import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PlaidLink } from "react-plaid-link";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";
import AlertContext from "../context/alert/alertContext";

/**
 * Component to connect to external financial institutions via Plaid API
 */
const ConnectAccounts = () => {
   //Global State
   const navigate = useNavigate();
   const { user, fetchAuthenticatedUser } = useContext(authContext);
   const { createAccount } = useContext(accountContext);
   const { setAlert } = useContext(AlertContext);
   const [accountAdded, setAccountAdded] = useState(false);

   //Functionality to map a given account type to our backend enum value
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

   //Functionality to handle a users successful connection of their financial institution via Plaid
   const handleOnSuccess = (publicToken, metadata) => {
      //Create paylaoad to send to backend
      const accountData = {
         plaidAccountId: metadata.account_id,
         accountName: metadata.institution.name,
         publicToken,
         accountType: mapAccountType(
            metadata.account.type,
            metadata.account.subtype
         ),
      };
      console.log(accountData);
      createAccount(accountData);

      setAlert("Account added succesfully", "SUCCESS");
      setAccountAdded(true);
   };

   //Functonality to handle a user closing Plaid Link
   const handleOnExit = (err, metadata) => {
      console.log("Error:", err);
      console.log("Metadata:", metadata);
   };

   //Functionality to navigate user to Income page
   const handleOnContinue = () => {
      navigate("/income");
   };

   if (!user || !user.linkToken) {
      // If user is not available or linkToken is missing, redirect to login
      navigate("/login");
   }

   //Fetch Authenticated User if Needed on Refresh
   useEffect(() => {
      if (!user && localStorage.token) {
         fetchAuthenticatedUser();
      }
   }, []);

   return (
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center">
         <div className="max-w-md text-center">
            <h1 className="text-3xl md:text-4xl font-bold mb-4 text-white xs:text-2xl">
               Connect Your Accounts
            </h1>
            {!accountAdded && (
               <p className="text-base md:text-lg mb-8 text-gray-400">
                  To get started, please connect your financial accounts using
                  Plaid.
               </p>
            )}
            <PlaidLink
               token={user?.linkToken.token}
               onSuccess={handleOnSuccess}
               onExit={handleOnExit}
               className="plaid-link-wrapper"
            >
               <div className="font-bold py-3 px-5 rounded text-white bg-indigo-600 hover:bg-indigo-700 xs:py-2 xs:px-4 xs:text-sm">
                  {accountAdded ? "Add Another Account" : "Connect Accounts"}
               </div>
            </PlaidLink>
            {accountAdded && (
               <div className="mt-10">
                  <button
                     onClick={handleOnContinue}
                     className="font-bold py-2 px-4 rounded text-white bg-green-600 hover:bg-green-700"
                  >
                     Continue...
                  </button>
               </div>
            )}
         </div>
         {!accountAdded &&
            <div className="w-full mt-3 flex justify-center">
               <button
                  onClick={handleOnContinue}
                  className="text-xs border border-indigo-300 font-bold py-2 px-3 rounded-2xl bg-indigo-300 mr-5 hover:bg-transparent hover:duration-500">
                  Skip For Now
               </button>
            </div>
         }
      </div>
   );
};

export default ConnectAccounts;
