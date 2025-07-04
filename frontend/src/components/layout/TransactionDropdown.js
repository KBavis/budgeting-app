import React, { useState } from "react";
import { Dropdown } from "react-bootstrap";
import { FaEllipsisV } from "react-icons/fa";
import "bootstrap/dist/css/bootstrap.min.css";

const TransactionDropdown = ({
   handleDeleteTransaction,
   handleSplitTransaction,
   handleReassignTransaction,
   handleRenameTransaction,
   handleReduceTransaction,
}) => {
   const [dropdownVisible, setDropdownVisible] = useState(false);

   const handleDropdownToggle = (isOpen) => {
      setDropdownVisible(isOpen);
   };

   return (
      <Dropdown show={dropdownVisible} onToggle={handleDropdownToggle}>
         <Dropdown.Toggle
            as="button"
            className="text-white bg-transparent border-none p-0 m-0"
         >
            <FaEllipsisV size={20} className="xs:w-4 xs:h-4"/>
         </Dropdown.Toggle>

         <Dropdown.Menu
            align="end"
            className="bg-white text-black text-sm text-center custom-dropdown-menu"
         >
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleDeleteTransaction}
            >
               Delete
            </Dropdown.Item>
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleSplitTransaction}
            >
               Split
            </Dropdown.Item>
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleReassignTransaction}
            >
               Assign Category
            </Dropdown.Item>
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleRenameTransaction}
            >
               Rename
            </Dropdown.Item>
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleReduceTransaction}
            >
               Reduce
            </Dropdown.Item>
         </Dropdown.Menu>
      </Dropdown>
   );
};

export default TransactionDropdown;
