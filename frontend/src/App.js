import React, { Fragment, useState, useEffect } from "react";
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
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
import IncomesPage from "./pages/IncomesPage";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import Navbar from "./components/layout/Navbar";
import ThemeToggleFab from "./components/layout/ThemeToggleFab";
import BudgetSummaryPage from "./pages/BudgetSummaryPage";
import SummaryState from "./context/summary/SummaryState";
import CategoryCreationPage from "./pages/CategoryCreationPage";
import SpendingAnalysisPage from "./pages/SpendingAnalysisPage";
import VenmoAutomationPage from "./pages/VenmoAutomationPage";
import CategoryPerformanceState from "./context/category/performances/CategoryPerformanceState";
import PrivateRoute from "./components/routing/PrivateRoute";

import { ThemeProvider } from "./context/theme/ThemeContext";

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
                                    <ThemeProvider>
                                       <Fragment>
                                          <Navbar />
                                          <ThemeToggleFab />
                                          <Alerts />
                                          <Routes>
                                             {/* Public Routes */}
                                             <Route
                                                path="/"
                                                element={<LoginRegisterPage />}
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

                                             {/* Protected Private Routes */}
                                             <Route
                                                path="/connect-accounts"
                                                element={
                                                   <PrivateRoute>
                                                      <ConnectAccounts />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/home"
                                                element={
                                                   <PrivateRoute>
                                                      <DndProvider backend={HTML5Backend}>
                                                         <HomePage />
                                                      </DndProvider>
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/income"
                                                element={
                                                   <PrivateRoute>
                                                      <IncomeInputPage />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/category-types"
                                                element={
                                                   <PrivateRoute>
                                                      <CategoryTypeInputPage />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/category/needs"
                                                element={
                                                   <PrivateRoute>
                                                      <CategoryCreationPage categoryType="Needs" />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/category/wants"
                                                element={
                                                   <PrivateRoute>
                                                      <CategoryCreationPage categoryType="Wants" />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/category/investments"
                                                element={
                                                   <PrivateRoute>
                                                      <CategoryCreationPage categoryType="Investments" />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/category/type/needs"
                                                element={
                                                   <PrivateRoute>
                                                      <CategoryTypePage categoryType="Needs" />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/category/type/wants"
                                                element={
                                                   <PrivateRoute>
                                                      <CategoryTypePage categoryType="Wants" />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/category/type/investments"
                                                element={
                                                   <PrivateRoute>
                                                      <CategoryTypePage categoryType="Investments" />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/budget/summary"
                                                element={
                                                   <PrivateRoute>
                                                      <BudgetSummaryPage />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/accounts"
                                                element={
                                                   <PrivateRoute>
                                                      <AccountsPage />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/income-streams"
                                                element={
                                                   <PrivateRoute>
                                                      <IncomesPage />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path="/venmo-automation"
                                                element={
                                                   <PrivateRoute>
                                                      <VenmoAutomationPage />
                                                   </PrivateRoute>
                                                }
                                             />
                                             <Route
                                                path=":type/analysis/:month/:year"
                                                element={
                                                   <PrivateRoute>
                                                      <SpendingAnalysisPage />
                                                   </PrivateRoute>
                                                }
                                             />

                                             {/* Catch-all fallback */}
                                             <Route path="*" element={<Navigate to="/" replace />} />
                                          </Routes>
                                       </Fragment>
                                    </ThemeProvider>
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
