import { useEffect, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { LogOut, ChevronsUpDown } from "lucide-react";

import sidebarItems from "../../constants/sidebar";
import Logo from "./Logo";
import { useAuth } from "../../hooks/useAuth";
import { getDoctorProfile } from "../../services/doctorProfileService";

import "./Sidebar.css";

export default function Sidebar() {
  const navigate = useNavigate();
  const { logout, doctor: authDoctor } = useAuth();

  const [doctor, setDoctor] = useState(null);

  const loadDoctorProfile = async () => {
    try {
      const data = await getDoctorProfile();
      setDoctor(data || null);
    } catch {
      setDoctor(null);
    }
  };

  useEffect(() => {
    loadDoctorProfile();

    const handleDoctorProfileUpdated = () => {
      loadDoctorProfile();
    };

    window.addEventListener("doctorProfileUpdated", handleDoctorProfileUpdated);
    return () => {
      window.removeEventListener("doctorProfileUpdated", handleDoctorProfileUpdated);
    };
  }, [authDoctor?.email]);

  const doctorName = doctor?.fullName?.trim() || authDoctor?.fullName?.trim() || "Doctor";
  const clinicName = doctor?.clinicHospital?.trim() || authDoctor?.clinicHospital?.trim() || "Clinic";

  const getInitials = (name) => {
    if (!name || name === "Doctor") return "DR";
    const words = name.trim().split(/\s+/).filter(Boolean);
    if (words.length === 0) return "DR";
    if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
    return (words[0].charAt(0) + words[words.length - 1].charAt(0)).toUpperCase();
  };

  const doctorInitials = getInitials(doctorName);

  return (
    <aside className="sidebar">
      {/* LOGO */}
      <div className="logo-section">
        <Logo />
      </div>

      {/* MENU */}
      <nav className="menu-list">
        {sidebarItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.title}
              to={item.path}
              end={item.path === "/"}
              className={({ isActive }) =>
                isActive ? "menu active" : "menu"
              }
            >
              <div className="menu-content">
                <Icon size={19} className="menu-icon" />
                <span className="menu-title">{item.title}</span>
              </div>

              {item.badge != null && (
                <span className="sidebar-pill-badge">{item.badge}</span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* PET PROMO CARD */}
      <div className="sidebar-pet-card">
        <div className="sidebar-pet-text">
          <p className="sidebar-pet-title">Caring for pets,</p>
          <p className="sidebar-pet-subtitle">made simple every day.</p>
        </div>
        <img
          src="/illustrations/sidebar-pet-heart.png"
          alt="Caring for pets heart"
          className="sidebar-pet-illustration"
        />
      </div>

      {/* DOCTOR PROFILE CARD */}
      <div className="sidebar-doctor-section">
        <div
          className="doctor-card"
          onClick={() => navigate("/settings")}
          title="Open doctor profile"
        >
          <div className="doctor-avatar">{doctorInitials}</div>

          <div className="doctor-info">
            <div className="doctor-info-header">
              <h4>{doctorName}</h4>
              <ChevronsUpDown size={14} className="doctor-chevron" />
            </div>
            <p className="doctor-clinic">{clinicName}</p>
            <div className="doctor-status">
              <span className="online-dot" />
              <span>Online</span>
            </div>
          </div>
        </div>

        {/* LOGOUT */}
        <button
          type="button"
          className="logout-btn"
          onClick={() => {
            logout();
            navigate("/login");
          }}
          title="Log out"
        >
          <LogOut size={16} />
          <span>Log out</span>
        </button>
      </div>
    </aside>
  );
}