import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { Menu, Sun, Moon, Plus } from "lucide-react";

import GlobalSearch from "./GlobalSearch";
import NotificationBell from "./NotificationBell";
import NewVisitModal from "../modals/NewVisitModal";

import { PAGE_META } from "../../constants/pageMeta";

import { getPatients } from "../../services/patientService";
import { getDoctorProfile } from "../../services/doctorProfileService";
import { useAuth } from "../../hooks/useAuth";
import { useTheme } from "../../hooks/useTheme";

import "./Navbar.css";

function greeting() {
  const h = new Date().getHours();

  if (h < 12) {
    return "Good Morning";
  }

  if (h < 17) {
    return "Good Afternoon";
  }

  return "Good Evening";
}

export default function Navbar() {
  const { pathname } = useLocation();
  const { doctor: authDoctor } = useAuth();
  const { isDark, toggleTheme } = useTheme();

  const [newVisitOpen, setNewVisitOpen] =
    useState(false);

  const [patientCount, setPatientCount] =
    useState(0);

  const [doctor, setDoctor] =
    useState(null);

  const [doctorLoading, setDoctorLoading] =
    useState(true);

  const [currentGreeting, setCurrentGreeting] =
    useState(greeting());

  useEffect(() => {
    // Automatically update greeting as time changes
    const timer = setInterval(() => {
      setCurrentGreeting(greeting());
    }, 30000);

    return () => clearInterval(timer);
  }, []);

  const isDashboard = pathname === "/";

  const meta = PAGE_META[pathname];

  // =====================================================
  // LOAD DOCTOR PROFILE
  // =====================================================

  const loadDoctorProfile = async () => {
    try {
      setDoctorLoading(true);

      const data =
        await getDoctorProfile();

      console.log(
        "Navbar - doctor profile:",
        data
      );

      setDoctor(data || null);
    } catch (error) {
      console.error(
        "Navbar - failed to load doctor profile:",
        error
      );

      setDoctor(null);
    } finally {
      setDoctorLoading(false);
    }
  };

  // =====================================================
  // LOAD DOCTOR ON START
  // AND WHEN PROFILE IS UPDATED
  // =====================================================

  useEffect(() => {
    loadDoctorProfile();

    const handleDoctorProfileUpdated = () => {
      console.log(
        "Navbar - doctor profile update detected"
      );

      loadDoctorProfile();
    };

    window.addEventListener(
      "doctorProfileUpdated",
      handleDoctorProfileUpdated
    );

    return () => {
      window.removeEventListener(
        "doctorProfileUpdated",
        handleDoctorProfileUpdated
      );
    };
  }, [authDoctor?.email]);

  // =====================================================
  // LOAD PATIENT COUNT
  // =====================================================

  useEffect(() => {
    if (pathname !== "/patients") {
      return;
    }

    const loadPatients = async () => {
      try {
        const data =
          await getPatients();

        setPatientCount(
          Array.isArray(data)
            ? data.length
            : 0
        );
      } catch (error) {
        console.error(
          "Navbar - failed to load patients:",
          error
        );

        setPatientCount(0);
      }
    };

    loadPatients();

    const refreshPatients = () => {
      loadPatients();
    };

    window.addEventListener(
      "patientsUpdated",
      refreshPatients
    );

    return () => {
      window.removeEventListener(
        "patientsUpdated",
        refreshPatients
      );
    };
  }, [pathname]);

  // =====================================================
  // DOCTOR NAME
  // =====================================================

  const doctorName =
    doctor?.fullName?.trim() ||
    authDoctor?.fullName?.trim() ||
    "Doctor";

  // =====================================================
  // CLINIC NAME
  // =====================================================

  const clinicName =
    doctor?.clinicHospital?.trim() ||
    authDoctor?.clinicHospital?.trim() ||
    "Clinic";

  // =====================================================
  // NAVBAR TITLE
  // =====================================================

  const title = isDashboard
    ? `${currentGreeting}, ${doctorName}`
    : meta?.title ||
      "Patients Profile";

  // =====================================================
  // NAVBAR SUBTITLE
  // =====================================================

  const subtitle = isDashboard
    ? clinicName
    : pathname === "/patients"
      ? `${patientCount} registered ${
          patientCount === 1
            ? "pet"
            : "pets"
        } across species`
      : meta?.subtitle || "";

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <>
      <header className="navbar">
        {/* LEFT SIDE */}
        <div className="navbar-left">
          <button type="button" className="navbar-toggle-btn" aria-label="Toggle menu">
            <Menu size={20} />
          </button>

          <div className="navbar-headings">
            <h1 className="navbar-title">
              {isDashboard ? (
                <>
                  {currentGreeting}, {doctorName}{" "}
                  <span className="navbar-hand-emoji">👏</span>
                </>
              ) : (
                title
              )}
            </h1>

            <p className="navbar-subtitle">
              {isDashboard
                ? "You're doing great! Here's what's happening in your practice today."
                : subtitle}
            </p>
          </div>
        </div>

        {/* CENTER SLEEPING PETS ARTWORK */}
        {isDashboard && (
          <div className="navbar-center-art" title="Caring for pets, enriching lives">
            <img
              src="/images/navbar-sleeping-pets-seamless.png"
              alt="Cute sleeping puppies and kitten"
              className="navbar-sleeping-pets-img"
            />
          </div>
        )}

        {/* RIGHT SIDE */}
        <div className="navbar-right">
          <div className="navbar-search">
            <GlobalSearch />
          </div>

          <NotificationBell />

          <button
            type="button"
            className="navbar-icon-btn"
            aria-label={isDark ? "Switch to light mode" : "Switch to dark mode"}
            title={isDark ? "Switch to light mode" : "Switch to dark mode"}
            onClick={toggleTheme}
          >
            {isDark ? <Sun size={18} /> : <Moon size={18} />}
          </button>

          <button
            type="button"
            className="navbar-btn-primary"
            onClick={() => setNewVisitOpen(true)}
          >
            <Plus size={16} strokeWidth={2.5} />
            <span>New Visit</span>
          </button>
        </div>
      </header>

      {/* =========================================== */}
      {/* NEW VISIT MODAL                            */}
      {/* =========================================== */}

      <NewVisitModal
        open={newVisitOpen}
        onClose={() =>
          setNewVisitOpen(false)
        }
      />
    </>
  );
}