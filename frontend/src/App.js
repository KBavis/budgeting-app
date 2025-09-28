import React, { Fragment, useState, useEffect } from "react";
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
import TransactionState from "./context/transaction/TransactionState";
import CategoryTypePage from "./pages/CategoryTypePage";
import AccountsPage from "./pages/AccountsPage";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import Navbar from "./components/layout/Navbar";
import BudgetSummaryPage from "./pages/BudgetSummaryPage";
import SummaryState from "./context/summary/SummaryState";
import CategoryCreationPage from "./pages/CategoryCreationPage";
import SpendingAnalysisPage from "./pages/SpendingAnalysisPage";
import CategoryPerformanceState from "./context/category/performances/CategoryPerformanceState";

/**
 *  Main Application File
 */
function App() {
   const [isInitialRender, setIsInitialRender] = useState(true);

   useEffect(() => {
      const timer = setTimeout(() => {
         setIsInitialRender(false);
      }, 1);

      return () => clearTimeout(timer);
   }, []);

   if (isInitialRender) {
      return null; // Or a loading spinner
   }

   return (
      <AlertState>
         <AuthState>
            <AccountState>
               <IncomeState>
                  <CategoryTypeState>
                     <CategoryState>
                        <TransactionState>
                           <SummaryState>
                              <CategoryPerformanceState>
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
                                          <Route
                                             path="/budget/summary"
                                             element={
                                                <BudgetSummaryPage />
                                             }
                                          />
                                          <Route
                                             path="/accounts"
                                             element={
                                                <AccountsPage />
                                             }
                                          />
                                          <Route
                                             path=":type/analysis/:month/:year"
                                             element={<SpendingAnalysisPage />}
                                          />
                                       </Routes>
                                    </Fragment>
                                 </Router>
                              </CategoryPerformanceState>
                           </SummaryState>
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
