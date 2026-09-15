import { Routes, Route } from "react-router-dom";

import DashboardLayout from "../layouts/DashboardLayout";
import PrivateRoute from "./PrivateRoute";
import PublicRoute from "./PublicRoute";

import Login from "../pages/Auth/Login";
import Register from "../pages/Auth/Register";
import ForgotPassword from "../pages/Auth/ForgotPassword";
import ResetPassword from "../pages/Auth/ResetPassword";

import Dashboard from "../pages/Dashboard/Dashboard";
import AppointmentPage from "../pages/Appointments/AppointmentPage";
import Patients from "../pages/Patients/Patients";
import PatientProfile from "../pages/Patients/PatientProfile";
import Prescription from "../pages/Prescription/Prescription";
import Followup from "../pages/Followups/Followup";
import Vaccination from "../pages/Vaccinations/Vaccination";
import Inventory from "../pages/Inventory/Inventory";
import Billing from "../pages/Billing/Billing";
import DoctorProfile from "../pages/Settings/DoctorProfile";

function AppRoutes() {
  return (
    <Routes>
      {/* -------------------- PUBLIC (AUTH) ROUTES -------------------- */}

      <Route
        path="/login"
        element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        }
      />

      <Route
        path="/register"
        element={
          <PublicRoute>
            <Register />
          </PublicRoute>
        }
      />

      <Route
        path="/forgot-password"
        element={
          <PublicRoute>
            <ForgotPassword />
          </PublicRoute>
        }
      />

      <Route
        path="/reset-password"
        element={
          <PublicRoute>
            <ResetPassword />
          </PublicRoute>
        }
      />

      {/* -------------------- PROTECTED DASHBOARD ROUTES -------------------- */}

      <Route
        element={
          <PrivateRoute>
            <DashboardLayout />
          </PrivateRoute>
        }
      >
        <Route path="/" element={<Dashboard />} />
        <Route path="/appointments" element={<AppointmentPage />} />
        <Route path="/patients" element={<Patients />} />
        <Route path="/patients/:id" element={<PatientProfile />} />
        <Route path="/prescriptions" element={<Prescription />} />
        <Route path="/followups" element={<Followup />} />
        <Route path="/vaccinations" element={<Vaccination />} />
        <Route path="/inventory" element={<Inventory />} />
        <Route path="/billing" element={<Billing />} />
        <Route path="/settings" element={<DoctorProfile />} />
        <Route path="*" element={<Dashboard />} />
      </Route>
    </Routes>
  );
}

export default AppRoutes;
