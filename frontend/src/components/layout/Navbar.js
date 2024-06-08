import React, { useState } from "react";
import { Link } from "react-router-dom";
import AddTransaction from "../transaction/AddTransaction";

const Navbar = () => {
   const [showModal, setShowModal] = useState(false);
   // Function to toggle the modal
   const toggleModal = () => {
      setShowModal(!showModal);
   };

   return (
      <nav className="absolute top-0 right-0 w-full p-3">
         <div className="container mx-auto">
            <ul className="flex justify-end space-x-8">
               <li>
                  <Link
                     to="/add-transaction"
                     className="text-white text-base font-bold duration-500 hover:text-indigo-400"
                  >
                     Transactions
                  </Link>
               </li>
               <li>
                  <Link
                     to="/add-account"
                     className="text-white text-base font-bold duration-500 hover:text-indigo-400"
                  >
                     Accounts
                  </Link>
               </li>
               <li>
                  <Link
                     to="/add-category"
                     className="text-white text-base font-bold duration-500 hover:text-indigo-400"
                  >
                     Categories
                  </Link>
               </li>
               <li>
                  <Link
                     to="/add-category"
                     className="text-white text-base font-bold duration-500 hover:text-indigo-400"
                  >
                     Logout
                  </Link>
               </li>
            </ul>
         </div>
         {showModal && <AddTransaction onClose={toggleModal} />}
      </nav>
   );
};

export default Navbar;
