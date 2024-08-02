import React, { useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import authContext from "../../context/auth/authContext";
import transactionContext from "../../context/transaction/transactionContext";

/**
 *  NavBar component to introduce Multipage Navigation and logging out capabilities
 */
const Navbar = () => {
  const { user, logout } = useContext(authContext);
  const { transactions } = useContext(transactionContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout(); // Call the logout function from the state
    navigate("/"); // Redirect to the home page
  };

  return (
    user &&
    transactions && (
      <nav className="absolute top-0 right-0 w-full py-8 z-[100]">
        <div className="container mx-auto">
          <ul className="flex justify-end space-x-12 text-sm">
            <li>
              <Link
                to="/transactions"
                className="text-white font-bold duration-500 hover:text-indigo-400"
              >
                Transactions
              </Link>
            </li>
            <li>
              <Link
                to="/accounts"
                className="text-white font-bold duration-500 hover:text-indigo-400"
              >
                Accounts
              </Link>
            </li>
            <li>
              <Link
                to="/budget/summary"
                className="text-white font-bold duration-500 hover:text-indigo-400"
              >
                Budget Summary
              </Link>
            </li>
            <li>
              <button
                onClick={handleLogout}
                className="text-white font-bold duration-500 hover:text-indigo-400"
              >
                Logout
              </button>
            </li>
          </ul>
        </div>
      </nav>
    )
  );
};

export default Navbar;
