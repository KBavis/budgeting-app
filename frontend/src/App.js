import AuthState from "./context/auth/AuthState";
import React, { Fragment, useEffect, useContext } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginRegisterPage from "./pages/LoginRegisterPage";
import HomePage from "./pages/HomePage";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import ForgotPassword from "./components/auth/ForgotPassword";
import authContext from "./context/auth/authContext";
import { useNavigate } from "react-router-dom";
import ConnectAccounts from "./pages/ConnectAccounts";

function App() {
   return (
      <AuthState>
         <Router>
            <Fragment>
               <Routes>
                  <Route
                     path="/connect-accounts"
                     element={<ConnectAccounts />}
                  />
                  <Route path="/" element={<LoginRegisterPage />} />
                  <Route path="/home" element={<HomePage />} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/register" element={<Register />} />
                  <Route path="/forgot-password" element={<ForgotPassword />} />
               </Routes>
            </Fragment>
         </Router>
      </AuthState>
   );
}

export default App;
