import AuthState from "./context/auth/AuthState";
import React, { Fragment } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginRegisterPage from "./pages/LoginRegisterPage";
import HomePage from "./pages/HomePage";

function App() {
   return (
      <AuthState>
         <Router>
            <Fragment>
               <Routes>
                  <Route path="/" element={<LoginRegisterPage />} />
                  <Route path="/home" element={<HomePage />} />
               </Routes>
            </Fragment>
         </Router>
      </AuthState>
   );
}

export default App;
