import React, { useContext, useEffect } from "react";
import { Navigate } from "react-router-dom";
import authContext from "../../context/auth/authContext";

/**
 * Component to protect private routes in the budget application.
 * Redirects unauthenticated users directly to the login page.
 */
const PrivateRoute = ({ children }) => {
   const { isAuthenticated, loading, fetchAuthenticatedUser, user } = useContext(authContext);
   const token = localStorage.getItem("token");

   useEffect(() => {
      if (token && !user && !isAuthenticated) {
         fetchAuthenticatedUser();
      }
   }, [token, user, isAuthenticated, fetchAuthenticatedUser]);

   if (!token && !isAuthenticated) {
      return <Navigate to="/login" replace />;
   }

   return children;
};

export default PrivateRoute;
