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
import CategoryState from "./context/category/CategoryState";
import CategoryCreationPage from "./pages/CategoryCreationPage";

function App() {
   return (
      <AlertState>
         <AuthState>
            <AccountState>
               <IncomeState>
                  <CategoryTypeState>
                     <CategoryState>
                        <Router>
                           <Fragment>
                              <Alerts />
                              <Routes>
                                 <Route
                                    path="/connect-accounts"
                                    element={<ConnectAccounts />}
                                 />
                                 <Route
                                    path="/"
                                    element={<LoginRegisterPage />}
                                 />
                                 <Route path="/home" element={<HomePage />} />
                                 <Route path="/login" element={<Login />} />
                                 <Route
                                    path="/register"
                                    element={<Register />}
                                 />
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
                                 <Route
                                    path="/category/needs"
                                    element={
                                       <CategoryCreationPage categoryType="Needs" />
                                    }
                                 />
                                 <Route
                                    path="/category/wants"
                                    element={
                                       <CategoryCreationPage categoryType="Wants" />
                                    }
                                 />
                                 <Route
                                    path="/category/investments"
                                    element={
                                       <CategoryCreationPage categoryType="Investments" />
                                    }
                                 />
                              </Routes>
                           </Fragment>
                        </Router>
                     </CategoryState>
                  </CategoryTypeState>
               </IncomeState>
            </AccountState>
         </AuthState>
      </AlertState>
   );
}

export default App;
