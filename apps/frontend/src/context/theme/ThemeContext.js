import React, { createContext, useState, useEffect, useContext, useMemo } from "react";
import { useLocation } from "react-router-dom";
import authContext from "../auth/authContext";
import categoryContext from "../category/categoryContext";
import categoryTypeContext from "../category/types/categoryTypeContext";

export const ThemeContext = createContext();

export const ThemeProvider = ({ children }) => {
   const { user, isAuthenticated } = useContext(authContext);
   const { categories } = useContext(categoryContext);
   const { categoryTypes } = useContext(categoryTypeContext);
   const location = useLocation();

   // Hidden routes during auth, registration, and initial onboarding steps
   // NOTE: useMemo ensures that array is created once rather than brand new Array each render of Provider 
   const hiddenRoutes = useMemo(
      () => [
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
      ],
      []
   );

   const isHiddenRoute = hiddenRoutes.includes(location.pathname);

   // Determine if user has completed registration / onboarding setup
   const isOnboarded = useMemo(() => {
      if (!categories || !categoryTypes || categories.length === 0 || categoryTypes.length === 0) {
         return false;
      }

      let categoryTypeIds = categoryTypes.map((type) => type.categoryTypeId);

      categories.forEach((category) => {
         const id = category.categoryTypeId;
         categoryTypeIds = categoryTypeIds.filter((typeId) => typeId !== id);
      });

      return categoryTypeIds.length === 0;
   }, [categories, categoryTypes]);

   // Theme switching is only enabled once user is authenticated AND completed registration
   const isThemeEnabled = Boolean(isAuthenticated && user && !isHiddenRoute && isOnboarded);

   // Load saved theme or default to dark
   const [theme, setTheme] = useState(() => {
      return localStorage.getItem("app_theme") || "dark";
   });

   useEffect(() => {
      if (isThemeEnabled) {
         localStorage.setItem("app_theme", theme);
         if (theme === "dark") {
            document.documentElement.classList.add("dark");
            document.documentElement.classList.remove("light");
         } else {
            document.documentElement.classList.add("light");
            document.documentElement.classList.remove("dark");
         }
      } else {
         // Default to dark mode styling during auth and registration steps
         document.documentElement.classList.add("dark");
         document.documentElement.classList.remove("light");
      }
   }, [theme, isThemeEnabled]);

   const toggleTheme = () => {
      if (!isThemeEnabled) return;
      setTheme((prev) => (prev === "dark" ? "light" : "dark"));
   };

   return (
      <ThemeContext.Provider value={{ theme, toggleTheme, isThemeEnabled }}>
         {children}
      </ThemeContext.Provider>
   );
};

export default ThemeContext;

