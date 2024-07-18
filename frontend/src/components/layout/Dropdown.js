import React, { useContext, useState, useEffect, useRef } from "react";
import { Dropdown, DropdownButton } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { FiDollarSign, FiList, FiUser, FiPlus } from "react-icons/fi";
import { PlaidLink } from "react-plaid-link";
import accountContext from "../../context/account/accountContext";
import AlertContext from "../../context/alert/alertContext";
import authContext from "../../context/auth/authContext";

const DropdownMenu = ({
   dropdownVisible,
   setDropdownVisible,
   handleShowAddTransactionModal,
   handleShowAddCategoryModal,
   user,
}) => {
   const handleDropdownToggle = (isOpen) => {
      setDropdownVisible(isOpen);
   };

   const handleAddAccountClick = () => {
      setDropdownVisible(false);
   };

   //Global State
   const { createAccount, accounts } = useContext(accountContext);
   const { setAlert } = useContext(AlertContext);
   const { refreshLinkToken } = useContext(authContext);

   //Local State
   const [accountAdded, setAccountAdded] = useState(null);
   const [plaidKey, setPlaidKey] = useState(Date.now());

   //Refrence for Refreshing Link Token
   const isTokenRefreshed = useRef(false);

   // Functionality to handle a user's successful connection of their financial institution via Plaid
   const handleOnSuccess = (publicToken, metadata) => {
      if (accountAdded) {
         if (accountAdded.plaidAccountId === metadata.account_id) {
            setAlert("Account already added", "danger");
            return;
         }
      }

      // Create payload to send to backend
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

      setAccountAdded(accountData);
      setAlert("Account added successfully", "SUCCESS");
      setPlaidKey(Date.now()); // Force PlaidLink to re-render by updating key
   };

   // Functionality to map a given account type to our backend enum value
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

   // Functionality to handle a user closing Plaid Link
   const handleOnExit = (err, metadata) => {
      console.log("Error:", err);
      console.log("Metadata:", metadata);
   };

   //Functionality to determine whether a User's link token is expired or not
   const isTokenExpired = (expirationDateTimeString) => {
      const now = new Date();
      const expiration = new Date(expirationDateTimeString);
      return now > expiration;
   };

   //Refresh User's Link Token if Needed to enable them to add additonal accounts
   useEffect(() => {
      if (
         !isTokenRefreshed.current &&
         user?.linkToken &&
         isTokenExpired(user.linkToken.expiration)
      ) {
         console.log(
            "Refreshing users link token due to link token being expired"
         );
         isTokenRefreshed.current = true; // Set the flag to true to prevent future refreshes
         refreshLinkToken(); // Refresh user's link token
      }
   }, [user, refreshLinkToken]);

   return (
      <DropdownButton
         className="absolute top-0 left-0 mt-4 ml-4 z-[105]"
         title={<FiPlus size={30} />}
         show={dropdownVisible}
         variant="secondary"
         onToggle={handleDropdownToggle}
      >
         <Dropdown.Item
            className="dropdown-item flex items-center mb-2 font-bold"
            onClick={() => {
               handleShowAddTransactionModal();
               setDropdownVisible(false);
            }}
         >
            <FiDollarSign className="mr-2" />
            Add Transaction
         </Dropdown.Item>
         <Dropdown.Item
            className="dropdown-item flex items-center mb-2 font-bold"
            onClick={() => {
               handleShowAddCategoryModal();
               setDropdownVisible(false);
            }}
         >
            <FiList className="mr-2" />
            Add Category
         </Dropdown.Item>
         <PlaidLink
            key={plaidKey} // Add key prop to force re-render
            token={user?.linkToken.token}
            onSuccess={handleOnSuccess}
            onExit={handleOnExit}
            className="plaid-link-wrapper-class"
         >
            <Dropdown.Item
               className="dropdown-item flex items-center font-bold"
               onClick={handleAddAccountClick}
            >
               <FiUser className="mr-2" />
               Add Account
            </Dropdown.Item>
         </PlaidLink>
      </DropdownButton>
   );
};

export default DropdownMenu;
