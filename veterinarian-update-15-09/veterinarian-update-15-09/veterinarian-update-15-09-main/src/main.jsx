import React from "react";
import ReactDOM from "react-dom/client";
import "./styles/globals.css";

import { BrowserRouter } from "react-router-dom";

import App from "./App";
import { AuthProvider } from "./context/AuthContext";
import { DoctorProvider } from "./context/DoctorContext";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <DoctorProvider>
          <App />
        </DoctorProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
