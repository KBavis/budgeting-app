import React, { useState, useContext } from "react";
import { Dropdown } from "react-bootstrap";
import { FaEllipsisV } from "react-icons/fa";
import { ThemeContext } from "../../context/theme/ThemeContext";
import "bootstrap/dist/css/bootstrap.min.css";

const TransactionDropdown = ({
   handleDeleteTransaction,
   handleSplitTransaction,
   handleReassignTransaction,
   handleRenameTransaction,
   handleReduceTransaction,
   className,
}) => {
   const [dropdownVisible, setDropdownVisible] = useState(false);
   const { theme } = useContext(ThemeContext);
   const isDark = theme === "dark";

   const handleDropdownToggle = (isOpen) => {
      setDropdownVisible(isOpen);
   };

   return (
      <Dropdown show={dropdownVisible} onToggle={handleDropdownToggle} className={className}>
         <Dropdown.Toggle
            as="button"
            className={`bg-transparent border-0 p-1 m-0 flex items-center justify-center transition-colors cursor-pointer ${
               isDark ? "text-slate-300 hover:text-white" : "text-slate-600 hover:text-slate-900"
            }`}
         >
            <FaEllipsisV size={16} className="xs:w-3.5 xs:h-3.5"/>
         </Dropdown.Toggle>

         <Dropdown.Menu
            align="end"
            className={`text-sm text-center shadow-xl border rounded-xl p-1 z-[1100] ${
               isDark
                  ? "bg-slate-900 text-slate-100 border-slate-700"
                  : "bg-white text-slate-800 border-slate-200"
            }`}
         >
            <Dropdown.Item
               className={`dropdown-item font-semibold py-2 px-4 rounded-lg cursor-pointer transition-colors ${
                  isDark ? "text-slate-200 hover:bg-indigo-600 hover:text-white" : "text-slate-700 hover:bg-indigo-50 hover:text-indigo-600"
               }`}
               onClick={handleDeleteTransaction}
            >
               Delete
            </Dropdown.Item>
            <Dropdown.Item
               className={`dropdown-item font-semibold py-2 px-4 rounded-lg cursor-pointer transition-colors ${
                  isDark ? "text-slate-200 hover:bg-indigo-600 hover:text-white" : "text-slate-700 hover:bg-indigo-50 hover:text-indigo-600"
               }`}
               onClick={handleSplitTransaction}
            >
               Split
            </Dropdown.Item>
            <Dropdown.Item
               className={`dropdown-item font-semibold py-2 px-4 rounded-lg cursor-pointer transition-colors ${
                  isDark ? "text-slate-200 hover:bg-indigo-600 hover:text-white" : "text-slate-700 hover:bg-indigo-50 hover:text-indigo-600"
               }`}
               onClick={handleReassignTransaction}
            >
               Assign Category
            </Dropdown.Item>
            <Dropdown.Item
               className={`dropdown-item font-semibold py-2 px-4 rounded-lg cursor-pointer transition-colors ${
                  isDark ? "text-slate-200 hover:bg-indigo-600 hover:text-white" : "text-slate-700 hover:bg-indigo-50 hover:text-indigo-600"
               }`}
               onClick={handleRenameTransaction}
            >
               Rename
            </Dropdown.Item>
            <Dropdown.Item
               className={`dropdown-item font-semibold py-2 px-4 rounded-lg cursor-pointer transition-colors ${
                  isDark ? "text-slate-200 hover:bg-indigo-600 hover:text-white" : "text-slate-700 hover:bg-indigo-50 hover:text-indigo-600"
               }`}
               onClick={handleReduceTransaction}
            >
               Reduce
            </Dropdown.Item>
         </Dropdown.Menu>
      </Dropdown>
   );
};

export default TransactionDropdown;
