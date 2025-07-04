import React, { useState } from "react";
import { Dropdown } from "react-bootstrap";
import { FaEllipsisV } from "react-icons/fa";
import "bootstrap/dist/css/bootstrap.min.css";

const CategoryDropdown = ({
   handleDeleteCategory,
   handleRenameCategory,
   handleUpdateAllocations,
}) => {
   const [dropdownVisible, setDropdownVisible] = useState(false);

   const handleDropdownToggle = (isOpen) => {
      setDropdownVisible(isOpen);
   };

   return (
      <Dropdown show={dropdownVisible} onToggle={handleDropdownToggle}>
         <Dropdown.Toggle
            as="button"
            className="text-black bg-transparent border-none p-0 m-0"
         >
            <FaEllipsisV size={20} className="xs:w-4 xs:h-4"/>
         </Dropdown.Toggle>

         <Dropdown.Menu
            align="end"
            className="bg-white text-black text-sm absolute text-center custom-dropdown-menu"
         >
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleDeleteCategory}
            >
               Delete
            </Dropdown.Item>
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleRenameCategory}
            >
               Rename
            </Dropdown.Item>
            <Dropdown.Item
               className="dropdown-item font-semibold"
               onClick={handleUpdateAllocations}
            >
               Update Allocations
            </Dropdown.Item>
         </Dropdown.Menu>
      </Dropdown>
   );
};

export default CategoryDropdown;
