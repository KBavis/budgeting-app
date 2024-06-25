import React, { useContext, useState, useEffect } from "react";
import { Dropdown, DropdownButton } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { FiDollarSign, FiList, FiUser, FiPlus } from "react-icons/fi";
import { PlaidLink } from "react-plaid-link";
import accountContext from "../../context/account/accountContext";
import AlertContext from "../../context/alert/alertContext";

const DropdownMenu = ({
   dropdownVisible,
   setDropdownVisible,
   handleShowAddTransactionModal,
   user,
}) => {
   const handleDropdownToggle = (isOpen) => {
      setDropdownVisible(isOpen);
   };

   const handleAddAccountClick = () => {
      setDropdownVisible(false);
   };

   const { createAccount, accounts } = useContext(accountContext);
   const [accountAdded, setAccountAdded] = useState(null);
   const { setAlert } = useContext(AlertContext);
   const [plaidKey, setPlaidKey] = useState(Date.now());

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

   //Re-Render When Accounts Added
   useEffect(() => {
      console.log(accounts);
   }, [accounts]);

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
               console.log("Add Category clicked");
               setDropdownVisible(false);
            }}
         >
            <FiList className="mr-2" />
            Add Category
         </Dropdown.Item>
         <PlaidLink
            key={plaidKey} // Add key prop to force re-render
            token={user?.linkToken}
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
