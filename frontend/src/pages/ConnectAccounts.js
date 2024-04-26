import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PlaidLink } from "react-plaid-link";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";
import AlertContext from "../context/alert/alertContext";

const ConnectAccounts = () => {
  const navigate = useNavigate();
  const { user } = useContext(authContext);
  const { createAccount } = useContext(accountContext);
  const { setAlert } = useContext(AlertContext);
  const [accountAdded, setAccountAdded] = useState(false);

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

  const handleOnSuccess = (publicToken, metadata) => {
    console.log("Public token:", publicToken);
    console.log("Account metadata:", metadata);

    // TODO: Send the publicToken to your server to exchange for an access token
    const accountData = {
      plaidAccountId: metadata.account_id,
      accountName: metadata.institution.name,
      publicToken,
      accountType: mapAccountType(
        metadata.account.type,
        metadata.account.subtype
      ),
    };
    createAccount(accountData);

    setAlert("Account added succesfully", "SUCCESS");
    setAccountAdded(true);
  };

  const handleOnExit = (err, metadata) => {
    console.log("Error:", err);
    console.log("Metadata:", metadata);
  };

  const handleOnEvent = (eventName, metadata) => {
    console.log("Event:", eventName);
    console.log("Metadata:", metadata);
  };

  const handleOnContinue = () => {
    navigate("/income");
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
        {!accountAdded && (
          <p className="text-lg mb-8 text-gray-400">
            To get started, please connect your financial accounts using Plaid.
          </p>
        )}
        <PlaidLink
          token={user.linkToken}
          onSuccess={handleOnSuccess}
          onExit={handleOnExit}
          onEvent={handleOnEvent}
          className="plaid-link-wrapper"
        >
          <div className="font-bold py-3 px-5 rounded text-white bg-indigo-600 hover:bg-indigo-700">
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
    </div>
  );
};

export default ConnectAccounts;
