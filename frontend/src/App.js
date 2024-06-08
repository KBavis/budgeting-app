import React, { Fragment } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AuthState from "./context/auth/AuthState";
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
import CategoryTypeState from "./context/category/types/CategoryTypeState";
import CategoryState from "./context/category/CategoryState";
import CategoryCreationPage from "./pages/CategoryCreationPage";
import TransactionState from "./context/transaction/TransactionState";
import CategoryTypePage from "./pages/CategoryTypePage";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import Navbar from "./components/layout/Navbar";

/**
 *  Main Application File
 */
function App() {
   return (
      <AlertState>
         <AuthState>
            <AccountState>
               <IncomeState>
                  <CategoryTypeState>
                     <CategoryState>
                        <TransactionState>
                           <Router>
                              <Fragment>
                                 <Navbar />
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
                                    <Route
                                       path="/home"
                                       element={
                                          <DndProvider backend={HTML5Backend}>
                                             <HomePage />
                                          </DndProvider>
                                       }
                                    />
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
                                    <Route
                                       path="/category/type/needs"
                                       element={
                                          <CategoryTypePage categoryType="Needs" />
                                       }
                                    />
                                    <Route
                                       path="/category/type/wants"
                                       element={
                                          <CategoryTypePage categoryType="Wants" />
                                       }
                                    />
                                    <Route
                                       path="/category/type/investments"
                                       element={
                                          <CategoryTypePage categoryType="Investments" />
                                       }
                                    />
                                 </Routes>
                              </Fragment>
                           </Router>
                        </TransactionState>
                     </CategoryState>
                  </CategoryTypeState>
               </IncomeState>
            </AccountState>
         </AuthState>
      </AlertState>
   );
}

export default App;
