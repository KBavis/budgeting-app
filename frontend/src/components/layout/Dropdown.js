import React from "react";
import { Dropdown, DropdownButton } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { FiDollarSign, FiList, FiUser, FiPlus } from "react-icons/fi";
import { PlaidLink } from "react-plaid-link";

const DropdownMenu = ({
   dropdownVisible,
   setDropdownVisible,
   handleShowAddTransactionModal,
   user,
   handleOnSuccess,
   handleOnExit,
}) => {
   const handleDropdownToggle = (isOpen) => {
      setDropdownVisible(isOpen);
   };

   const handleAddAccountClick = () => {
      setDropdownVisible(false);
   };

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
            token={user?.linkToken}
            onSuccess={handleOnSuccess}
            onExit={handleOnExit}
            clientName="plaid-link-wrapper"
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
