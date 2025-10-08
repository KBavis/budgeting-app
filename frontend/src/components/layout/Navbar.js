import React, { useContext, useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import authContext from "../../context/auth/authContext";
import transactionContext from "../../context/transaction/transactionContext";
import categoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";

/**
 *  NavBar component to introduce Multipage Navigation and logging out capabilities
 */
const Navbar = () => {
  const { user, logout } = useContext(authContext);
  const { transactions } = useContext(transactionContext);
  const { categories } = useContext(categoryContext)
  const { categoryTypes } = useContext(categoryTypeContext)
  const navigate = useNavigate();
  const [isScrolled, setIsScrolled] = useState(false);
  const [isOnboarded, setIsOnboarded] = useState(false)


  const handleLogout = () => {
    logout(); // Call the logout function from the state
    navigate("/"); // Redirect to the home page
  };

  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 0) {
        setIsScrolled(true);
      } else {
        setIsScrolled(false);
      }
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, []);

  // use effect to determine if user has completed onboarding 
  useEffect(() => {
    if (!categories || !categoryTypes) {
      return false;
    }

    // extract relevant category type Ids
    let categoryTypeIds = categoryTypes.map((type) => type.categoryTypeId)

    // Iterate through categories and remove matched categoryTypeIds
    categories.forEach((category) => {
      const id = category.categoryType?.categoryTypeId;
      // remove the id if it exists in categoryTypeIds
      categoryTypeIds = categoryTypeIds.filter((typeId) => typeId !== id);
    });

    setIsOnboarded(categoryTypeIds.length == 0)
  }, [categories, categoryTypes])

  return (
    isOnboarded && user && (
      <nav
        className={`fixed top-0 left-0 w-full z-50 py-4 transition-colors duration-300 ${isScrolled ? "bg-indigo-600" : "bg-transparent"
          }`}
      >
        <div className="container mx-auto flex justify-end">
          <ul className="flex space-x-6">
            <li>
              <Link
                to="/accounts"
                className="text-white text-base font-bold hover:text-indigo-400 xs:text-sm"
              >
                Accounts
              </Link>
            </li>
            <li>
              <Link
                to="/budget/summary"
                className="text-white text-base font-bold hover:text-indigo-400 xs:text-sm"
              >
                History
              </Link>
            </li>
            <li>
              <button
                onClick={handleLogout}
                className="text-white text-base font-bold hover:text-indigo-400 xs:text-sm"
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
