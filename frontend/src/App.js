import AuthState from "./context/auth/AuthState";
import React, { Fragment, useEffect, useContext } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginRegisterPage from "./pages/LoginRegisterPage";
import HomePage from "./pages/HomePage";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import ForgotPassword from "./components/auth/ForgotPassword";
import ConnectAccounts from "./pages/ConnectAccounts";
import AlertState from "./context/alert/AlertState";
import Alerts from "./components/alert/Alert";
import AccountState from "./context/account/AccountState";
import IncomeInputPage from "./pages/IncomeInputPage";
import IncomeState from "./context/income/IncomeState";
import CategoryTypeInputPage from "./pages/CategoryTypeInputPage";
import CategoryTypeState from "./context/categoryTypes/CategoryTypeState";

function App() {
   return (
      <AlertState>
         <AuthState>
            <AccountState>
               <IncomeState>
                  <CategoryTypeState>
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
                              <Route
                                 path="/forgot-password"
                                 element={<ForgotPassword />}
                              />
                              <Route
                                 path="/income"
                                 element={<IncomeInputPage />}
                              />
                              <Route
                                 path="/category-types"
                                 element={<CategoryTypeInputPage />}
                              />
                           </Routes>
                        </Fragment>
                     </Router>
                  </CategoryTypeState>
               </IncomeState>
            </AccountState>
         </AuthState>
      </AlertState>
   );
}

export default App;
