import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import {
  FiUser,
  FiMail,
  FiPhone,
  FiLock,
  FiEye,
  FiEyeOff,
  FiAlertCircle,
} from "react-icons/fi";

import Button from "../../components/ui/Button";
import { Field } from "../../components/ui/Input";
import AuthLayout from "../../layouts/AuthLayout";
import { useAuth } from "../../hooks/useAuth";
import { ROUTES } from "../../constants/routes";

import "./Auth.css";

export default function Register() {
  const navigate = useNavigate();
  const { register: createAccount } = useAuth();

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState("");

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    defaultValues: {
      fullName: "",
      email: "",
      phone: "",
      password: "",
      confirmPassword: "",
    },
  });

  const password = watch("password");

  const onSubmit = async (values) => {
    setServerError("");
    setSubmitting(true);

    try {
      const data = await createAccount({
        fullName: values.fullName.trim(),
        email: values.email.trim().toLowerCase(),
        phone: values.phone.trim(),
        password: values.password,
      });

      if (data?.token) {
        // Already-approved / auto-login path, if the backend ever issues one.
        toast.success(`Account created. Welcome, Dr. ${data?.fullName?.split(" ")[0] || ""}`.trim());
        navigate(ROUTES.DASHBOARD, { replace: true });
        return;
      }

      // Normal path: account is created but pending admin approval, so send
      // the doctor to Login with a clear message instead of the dashboard.
      toast.success(
        data?.message ||
          "Account created. It's pending admin approval - you'll be able to log in once approved.",
        { duration: 6000 }
      );

      navigate(ROUTES.LOGIN, { replace: true });
    } catch (error) {
      const message =
        error?.response?.data?.message ||
        "Could not create your account. Please try again.";

      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout>
      <div className="auth-form-header">
        <h2>Create Doctor Account</h2>
        <p>Set up your veterinary dashboard in a couple of minutes.</p>
      </div>

      {serverError && (
        <div className="auth-alert" style={{ marginBottom: 18 }}>
          <FiAlertCircle size={16} style={{ flexShrink: 0, marginTop: 1 }} />
          <span>{serverError}</span>
        </div>
      )}

      <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
        <Field label="Full name">
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiUser size={16} />
            </span>

            <input
              type="text"
              className={`input${errors.fullName ? " input-error" : ""}`}
              placeholder="Dr. Jane Doe"
              autoComplete="name"
              {...register("fullName", {
                required: "Full name is required",
                minLength: { value: 3, message: "Enter your full name" },
              })}
            />
          </div>
          {errors.fullName && <span className="field-error">{errors.fullName.message}</span>}
        </Field>

        <Field label="Email address">
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

        <Field label="Phone number">
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiPhone size={16} />
            </span>

            <input
              type="tel"
              className={`input${errors.phone ? " input-error" : ""}`}
              placeholder="9876543210"
              autoComplete="tel"
              {...register("phone", {
                required: "Phone number is required",
                pattern: {
                  value: /^[0-9+\-\s]{7,15}$/,
                  message: "Enter a valid phone number",
                },
              })}
            />
          </div>
          {errors.phone && <span className="field-error">{errors.phone.message}</span>}
        </Field>

        <div className="auth-form-row">
          <Field label="Password">
            <div className="auth-input-wrap">
              <span className="auth-input-icon">
                <FiLock size={16} />
              </span>

              <input
                type={showPassword ? "text" : "password"}
                className={`input${errors.password ? " input-error" : ""}`}
                placeholder="Min. 6 characters"
                autoComplete="new-password"
                style={{ paddingRight: 44 }}
                {...register("password", {
                  required: "Password is required",
                  minLength: { value: 6, message: "At least 6 characters" },
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

          <Field label="Confirm password">
            <div className="auth-input-wrap">
              <span className="auth-input-icon">
                <FiLock size={16} />
              </span>

              <input
                type={showConfirm ? "text" : "password"}
                className={`input${errors.confirmPassword ? " input-error" : ""}`}
                placeholder="Re-enter password"
                autoComplete="new-password"
                style={{ paddingRight: 44 }}
                {...register("confirmPassword", {
                  required: "Please confirm your password",
                  validate: (value) => value === password || "Passwords do not match",
                })}
              />

              <button
                type="button"
                className="auth-input-toggle"
                onClick={() => setShowConfirm((prev) => !prev)}
                tabIndex={-1}
                aria-label={showConfirm ? "Hide password" : "Show password"}
              >
                {showConfirm ? <FiEyeOff size={16} /> : <FiEye size={16} />}
              </button>
            </div>
            {errors.confirmPassword && (
              <span className="field-error">{errors.confirmPassword.message}</span>
            )}
          </Field>
        </div>

        <Button
          type="submit"
          size="lg"
          className="auth-submit"
          disabled={submitting}
        >
          {submitting ? "Creating account..." : "Create account"}
        </Button>
      </form>

      <p className="auth-form-footer">
        Already have an account?{" "}
        <Link to={ROUTES.LOGIN} className="auth-link">
          Sign in
        </Link>
      </p>
    </AuthLayout>
  );
}
