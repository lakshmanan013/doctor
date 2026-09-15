import {
  FiCalendar,
  FiFileText,
  FiPackage,
  FiShield,
} from "react-icons/fi";

import "../pages/Auth/Auth.css";

export default function AuthLayout({ children }) {
  return (
    <div className="auth-shell">

      {/* -------------------- BRAND HERO PANEL -------------------- */}
      <div className="auth-brand">

        {/* Ambient Background Decorative Motifs */}
        <div className="auth-brand-bg-decor" aria-hidden="true">
          {/* Top Left Floating Paws */}
          <svg
            className="auth-bg-paw auth-bg-paw-1"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path d="M12 8.5c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm-4 1.5c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm8 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm-1.5 5.5c-.8-.8-1.7-1-2.5-1s-1.7.2-2.5 1c-1.4 1.4-1.5 3.5 0 4.9 1.4 1.4 3.6 1.4 5 0 1.5-1.4 1.4-3.5 0-4.9z" />
          </svg>
          <svg
            className="auth-bg-paw auth-bg-paw-2"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path d="M12 8.5c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm-4 1.5c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm8 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm-1.5 5.5c-.8-.8-1.7-1-2.5-1s-1.7.2-2.5 1c-1.4 1.4-1.5 3.5 0 4.9 1.4 1.4 3.6 1.4 5 0 1.5-1.4 1.4-3.5 0-4.9z" />
          </svg>

          {/* Glowing Neon Heart with Medical Cross */}
          <div className="auth-bg-heart-cross">
            <svg
              viewBox="0 0 100 100"
              fill="none"
              stroke="currentColor"
              strokeWidth="4"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M50 85 C20 62 10 45 10 30 C10 18 20 10 32 10 C40 10 47 15 50 22 C53 15 60 10 68 10 C80 10 90 18 90 30 C90 45 80 62 50 85 Z" />
              <path d="M50 32 V54 M39 43 H61" strokeWidth="5" />
            </svg>
          </div>

          {/* Bottom Left Subtle Paw */}
          <svg
            className="auth-bg-paw auth-bg-paw-3"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path d="M12 8.5c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm-4 1.5c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm8 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm-1.5 5.5c-.8-.8-1.7-1-2.5-1s-1.7.2-2.5 1c-1.4 1.4-1.5 3.5 0 4.9 1.4 1.4 3.6 1.4 5 0 1.5-1.4 1.4-3.5 0-4.9z" />
          </svg>
        </div>

        {/* Brand Top Header */}
        <div className="auth-brand-top">
          <div className="auth-brand-logo-icon">
            <img
              src="/zenve.png"
              alt="Zenve Logo"
              className="auth-zenve-logo"
            />
          </div>
          <div className="auth-brand-logo-text">
            <h3>Zenve Doctors</h3>
            <span className="auth-brand-badge-tag">Veterinary Practice OS</span>
          </div>
        </div>

        {/* Hero Visual Presentation (Dog & Cat with Stethoscope) */}
        <div className="auth-brand-visual-container">
          <img
            src="/images/login-pets-hero.png"
            alt="Veterinary practice care with dog and cat"
            className="auth-brand-visual-img"
          />
          <div className="auth-brand-visual-gradient-mask" />
        </div>

        {/* Hero Mid Content */}
        <div className="auth-brand-mid">
          <h1 className="auth-hero-title">
            Run your clinic<br />
            from one simple<br />
            <span className="auth-hero-highlight">dashboard.</span>
          </h1>

          <p className="auth-hero-desc">
            Manage appointments, patients, prescriptions, inventory and
            billing in one place, built for busy veterinary practices.
          </p>

          <div className="auth-brand-features">
            <div className="auth-brand-feature">
              <div className="auth-brand-feature-icon">
                <FiCalendar size={18} />
              </div>
              <span className="auth-brand-feature-text">
                Smart appointment<br />scheduling
              </span>
            </div>

            <div className="auth-brand-feature">
              <div className="auth-brand-feature-icon">
                <FiFileText size={18} />
              </div>
              <span className="auth-brand-feature-text">
                Digital prescriptions<br />& records
              </span>
            </div>

            <div className="auth-brand-feature">
              <div className="auth-brand-feature-icon">
                <FiPackage size={18} />
              </div>
              <span className="auth-brand-feature-text">
                Inventory & billing,<br />handled.
              </span>
            </div>
          </div>
        </div>

        {/* Brand Bottom Footer */}
        <div className="auth-brand-bottom">
          <span>&copy; Zenve Technologies Inc.</span>
          <span>v2.4.1</span>
        </div>

      </div>

      {/* -------------------- FORM PANEL -------------------- */}
      <div className="auth-form-panel">
        <div className="auth-form-wrap">
          {children}
        </div>
      </div>

    </div>
  );
}