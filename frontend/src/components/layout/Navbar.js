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
      <nav className="w-full z-10 py-4">
        <div className="container mx-auto flex justify-end">
          <ul className="flex space-x-6">
            <li>
              <Link
                to="/accounts"
                className="text-white text-sm font-bold hover:text-indigo-400"
              >
                Accounts
              </Link>
            </li>
            <li>
              <Link
                to="/budget/summary"
                className="text-white text-sm font-bold hover:text-indigo-400 xs:text-xxs"
              >
                Budget Summary
              </Link>
            </li>
            <li>
              <button
                onClick={handleLogout}
                className="text-white text-sm font-bold hover:text-indigo-400"
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
