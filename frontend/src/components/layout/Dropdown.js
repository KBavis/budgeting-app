import React from "react";
import { Dropdown, DropdownButton } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { FiDollarSign, FiList, FiUser, FiPlus } from "react-icons/fi";

const DropdownMenu = ({
   dropdownVisible,
   setDropdownVisible,
   handleShowAddTransactionModal,
}) => {
   return (
      <DropdownButton
         className="absolute top-0 left-0 mt-4 ml-4 z-[500] "
         title={<FiPlus size={30} />}
         show={dropdownVisible}
         onToggle={() => setDropdownVisible(!dropdownVisible)}
      >
         <Dropdown.Item
            className="dropdown-item flex items-center mb-2 font-bold"
            onClick={handleShowAddTransactionModal}
         >
            <FiDollarSign className="mr-2" />
            Add Transaction
         </Dropdown.Item>
         <Dropdown.Item
            className="dropdown-item flex items-center mb-2 font-bold"
            onClick={() => console.log("Add Category clicked")}
         >
            <FiList className="mr-2" />
            Add Category
         </Dropdown.Item>
         <Dropdown.Item
            className="dropdown-item flex items-center font-bold"
            onClick={() => console.log("Add Account clicked")}
         >
            <FiUser className="mr-2" />
            Add Account
         </Dropdown.Item>
      </DropdownButton>
   );
};

export default DropdownMenu;
