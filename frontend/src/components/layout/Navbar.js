import React, { useContext, useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import authContext from "../../context/auth/authContext";
import transactionContext from "../../context/transaction/transactionContext";
import categoryContext from "../../context/category/categoryContext";
import categoryTypeContext from "../../context/category/types/categoryTypeContext";
import { ThemeContext } from "../../context/theme/ThemeContext";

/**
 * NavBar component with adaptive light/dark glassmorphic navbar styling,
 * generous padding, and clean no-underline typography.
 * Automatically hidden during initial registration & onboarding steps.
 */
const Navbar = () => {
  const { user, logout } = useContext(authContext);
  const { transactions } = useContext(transactionContext);
  const { categories } = useContext(categoryContext);
  const { categoryTypes } = useContext(categoryTypeContext);
  const { theme } = useContext(ThemeContext);

  const isDark = theme === "dark";
  const location = useLocation();
  const navigate = useNavigate();

  const [isScrolled, setIsScrolled] = useState(false);
  const [isOnboarded, setIsOnboarded] = useState(false);

  // Hidden routes during auth, registration, and initial onboarding steps
  const hiddenRoutes = [
    "/",
    "/login",
    "/register",
    "/connect-accounts",
    "/income",
    "/category-types",
    "/category/needs",
    "/category/wants",
    "/category/investments",
    "/forgot-password",
  ];

  const isHiddenRoute = hiddenRoutes.includes(location.pathname);

  const handleLogout = () => {
    logout();
    navigate("/");
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

  useEffect(() => {
    if (!categories || !categoryTypes) {
      return false;
    }

    let categoryTypeIds = categoryTypes.map((type) => type.categoryTypeId);

    categories.forEach((category) => {
      const id = category.categoryType?.categoryTypeId;
      categoryTypeIds = categoryTypeIds.filter((typeId) => typeId !== id);
    });

    setIsOnboarded(categoryTypeIds.length === 0);
  }, [categories, categoryTypes]);

  // Do not render navbar on auth / registration / onboarding routes
  const mainPages = ["/home", "/accounts", "/income-streams", "/budget/summary"];
  const isMainPage = mainPages.includes(location.pathname);

  if (isHiddenRoute || !user || (!isOnboarded && !isMainPage && categories && categoryTypes)) {
    return null;
  }

  return (
    <nav
      className={`fixed top-0 left-0 w-full z-[200] py-3.5 transition-all duration-300 pointer-events-auto border-b ${
        isScrolled
          ? isDark
            ? "bg-slate-900/95 backdrop-blur-md shadow-lg border-slate-800/80"
            : "bg-white/95 backdrop-blur-md shadow-md border-slate-200/80"
          : "bg-transparent border-transparent"
      }`}
    >
      <div className="container mx-auto flex justify-end items-center px-6">
        <ul className="flex space-x-7 items-center m-0 p-0 list-none">
          <li>
            <Link
              to="/home"
              className={`text-sm font-bold no-underline transition-colors ${
                isDark
                  ? "text-slate-100 hover:text-indigo-400"
                  : "text-slate-800 hover:text-indigo-600"
              }`}
              style={{ textDecoration: 'none' }}
            >
              Home
            </Link>
          </li>
          <li>
            <Link
              to="/accounts"
              className={`text-sm font-bold no-underline transition-colors ${
                isDark
                  ? "text-slate-100 hover:text-indigo-400"
                  : "text-slate-800 hover:text-indigo-600"
              }`}
              style={{ textDecoration: 'none' }}
            >
              Accounts
            </Link>
          </li>
          <li>
            <Link
              to="/income-streams"
              className={`text-sm font-bold no-underline transition-colors ${
                isDark
                  ? "text-slate-100 hover:text-indigo-400"
                  : "text-slate-800 hover:text-indigo-600"
              }`}
              style={{ textDecoration: 'none' }}
            >
              Income
            </Link>
          </li>
          <li>
            <Link
              to="/budget/summary"
              className={`text-sm font-bold no-underline transition-colors ${
                isDark
                  ? "text-slate-100 hover:text-indigo-400"
                  : "text-slate-800 hover:text-indigo-600"
              }`}
              style={{ textDecoration: 'none' }}
            >
              Budget
            </Link>
          </li>
          <li>
            <button
              onClick={handleLogout}
              className={`text-sm font-bold no-underline transition-colors bg-transparent border-0 cursor-pointer p-0 ${
                isDark
                  ? "text-slate-100 hover:text-indigo-400"
                  : "text-slate-800 hover:text-indigo-600"
              }`}
              style={{ textDecoration: 'none' }}
            >
              Logout
            </button>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
