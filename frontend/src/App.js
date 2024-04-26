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
import AlertState from "./context/alert/AlertState";
import Alerts from "./components/alert/Alert";
import AccountState from "./context/account/AccountState";
import IncomeInputPage from "./pages/IncomeInputPage";
import IncomeState from "./context/income/IncomeState";

function App() {
  return (
    <AlertState>
      <AuthState>
        <AccountState>
          <IncomeState>
            <Router>
              <Fragment>
                <Alerts />
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
                  <Route path="/income" element={<IncomeInputPage />} />
                </Routes>
              </Fragment>
            </Router>
          </IncomeState>
        </AccountState>
      </AuthState>
    </AlertState>
  );
}

export default App;
