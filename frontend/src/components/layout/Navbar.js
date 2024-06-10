import React, { useContext } from "react";
import { Link } from "react-router-dom";
import authContext from "../../context/auth/authContext";

/**
 *  NavBar component to introduce Multipage Navigation and logging out capabilities
 */
const Navbar = () => {
  const { user } = useContext(authContext);

  return (
    user && (
      <nav className="absolute top-0 right-0 w-full py-8 z-[100]">
        <div className="container mx-auto">
          <ul className="flex justify-end space-x-8 text-sm">
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
                to="/categories"
                className="text-white font-bold duration-500 hover:text-indigo-400"
              >
                Categories
              </Link>
            </li>
            <li>
              <Link
                to="/logout"
                className="text-white font-bold duration-500 hover:text-indigo-400"
              >
                Logout
              </Link>
            </li>
          </ul>
        </div>
      </nav>
    )
  );
};

export default Navbar;
