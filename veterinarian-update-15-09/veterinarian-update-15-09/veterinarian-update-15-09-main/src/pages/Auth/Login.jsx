import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { FiMail, FiLock, FiEye, FiEyeOff, FiAlertCircle, FiShield } from "react-icons/fi";

import Button from "../../components/ui/Button";
import { Field } from "../../components/ui/Input";
import AuthLayout from "../../layouts/AuthLayout";
import { useAuth } from "../../hooks/useAuth";
import { ROUTES } from "../../constants/routes";

import "./Auth.css";

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [showPassword, setShowPassword] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState("");

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: { email: "", password: "" },
  });

  const redirectTo = location.state?.from?.pathname || ROUTES.DASHBOARD;

  const onSubmit = async (values) => {
    setServerError("");
    setSubmitting(true);

    try {
      const data = await login({
        email: values.email.trim().toLowerCase(),
        password: values.password,
      });

      toast.success(`Welcome back, Dr. ${data?.fullName?.split(" ")[0] || ""}`.trim());

      navigate(redirectTo, { replace: true });
    } catch (error) {
      const message =
        error?.response?.data?.message ||
        "Invalid email or password. Please try again.";

      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout>
      <div className="auth-form-header">
        <div className="auth-header-badge">
          <svg
            className="auth-paw-cross-svg"
            viewBox="0 0 48 48"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            {/* 4 Paw Toes */}
            <ellipse cx="14" cy="16" rx="3.5" ry="5.5" transform="rotate(-15 14 16)" fill="#3730a3" />
            <ellipse cx="21" cy="11.5" rx="3.5" ry="5.5" transform="rotate(-5 21 11.5)" fill="#3730a3" />
            <ellipse cx="29" cy="11.5" rx="3.5" ry="5.5" transform="rotate(5 29 11.5)" fill="#3730a3" />
            <ellipse cx="36" cy="16" rx="3.5" ry="5.5" transform="rotate(15 36 16)" fill="#3730a3" />
            
            {/* Main Paw Pad */}
            <path
              d="M15 28C13.5 32.5 16.5 39 25 39C33.5 39 36.5 32.5 35 28C33.5 23.5 28.5 23.5 25 25.5C21.5 23.5 16.5 23.5 15 28Z"
              fill="#3730a3"
            />
            {/* White Medical Cross inside main pad */}
            <path
              d="M23.5 29H26.5V32H29.5V35H26.5V38H23.5V35H20.5V32H23.5V29Z"
              fill="#FFFFFF"
            />
          </svg>
        </div>

        <h2>Welcome Doctor</h2>
        <p>Sign in to manage your clinic, patients, and schedule.</p>
      </div>

      {serverError && (
        <div className="auth-alert" style={{ marginBottom: 18 }}>
          <FiAlertCircle size={16} style={{ flexShrink: 0, marginTop: 1 }} />
          <span>{serverError}</span>
        </div>
      )}

      <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
        <Field label="Email address" required>
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiMail size={16} />
            </span>

            <input
              type="email"
              className={`input${errors.email ? " input-error" : ""}`}
              placeholder="you@clinic.com"
              autoComplete="email"
              {...register("email", {
                required: "Email is required",
                pattern: {
                  value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                  message: "Enter a valid email address",
                },
              })}
            />
          </div>
          {errors.email && <span className="field-error">{errors.email.message}</span>}
        </Field>

        <Field label="Password" required>
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiLock size={16} />
            </span>

            <input
              type={showPassword ? "text" : "password"}
              className={`input${errors.password ? " input-error" : ""}`}
              placeholder="Enter your password"
              autoComplete="current-password"
              style={{ paddingRight: 44 }}
              {...register("password", {
                required: "Password is required",
              })}
            />

            <button
              type="button"
              className="auth-input-toggle"
              onClick={() => setShowPassword((prev) => !prev)}
              tabIndex={-1}
              aria-label={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? <FiEyeOff size={16} /> : <FiEye size={16} />}
            </button>
          </div>
          {errors.password && <span className="field-error">{errors.password.message}</span>}
        </Field>

        <div className="auth-form-meta">
          <label className="auth-checkbox">
            <input type="checkbox" {...register("remember")} />
            Remember me
          </label>

          <Link to="/forgot-password" className="auth-link">
            Forgot password?
          </Link>
        </div>

        <Button
          type="submit"
          size="lg"
          className="auth-submit"
          disabled={submitting}
        >
          {submitting ? "Signing in..." : "Sign in"}
        </Button>
      </form>

      <p className="auth-form-footer">
        Don&apos;t have an account?{" "}
        <Link to={ROUTES.REGISTER} className="auth-link">
          Create one
        </Link>
      </p>

      {/* Security Assurance Badge */}
      <div className="auth-security-badge">
        <span className="auth-security-line" />
        <span className="auth-security-content">
          <FiShield className="auth-security-icon" size={16} />
          Secure veterinary practice management
        </span>
        <span className="auth-security-line" />
      </div>
    </AuthLayout>
  );
}
