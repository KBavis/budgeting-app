import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { PlaidLink } from "react-plaid-link";
import authContext from "../context/auth/authContext";

const ConnectAccounts = () => {
   const navigate = useNavigate();
   const { user } = useContext(authContext);
   //constt { createAccount } =useContext(accountContext);

   const handleOnSuccess = (publicToken, metadata) => {
      console.log("Public token:", publicToken);
      console.log("Account metadata:", metadata);
      // TODO: Send the publicToken to your server to exchange for an access token

      navigate("/home");
   };

   const handleOnExit = (err, metadata) => {
      console.log("Error:", err);
      console.log("Metadata:", metadata);
   };

   const handleOnEvent = (eventName, metadata) => {
      console.log("Event:", eventName);
      console.log("Metadata:", metadata);
   };

   if (!user || !user.linkToken) {
      // If user is not available or linkToken is missing, redirect to login
      navigate("/login");
      return null;
   }

   return (
      <div className="flex min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 justify-center items-center">
         <div className="max-w-md text-center">
            <h1 className="text-4xl font-bold mb-4 text-white">
               Connect Your Accounts
            </h1>
            <p className="text-lg mb-8 text-gray-400">
               To get started, please connect your financial accounts using
               Plaid.
            </p>
            <PlaidLink
               token={user.linkToken}
               onSuccess={handleOnSuccess}
               onExit={handleOnExit}
               onEvent={handleOnEvent}
               className="plaid-link-wrapper"
            >
               <div className="font-bold py-3 px-5 rounded text-white">
                  Connect Accounts
               </div>
            </PlaidLink>
         </div>
      </div>
   );
};

export default ConnectAccounts;
