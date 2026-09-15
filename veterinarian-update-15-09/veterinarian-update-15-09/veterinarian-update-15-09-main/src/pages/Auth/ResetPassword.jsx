import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { FiLock, FiEye, FiEyeOff, FiAlertCircle } from "react-icons/fi";

import Button from "../../components/ui/Button";
import { Field } from "../../components/ui/Input";
import AuthLayout from "../../layouts/AuthLayout";
import { resetPassword } from "../../services/authService";
import { ROUTES } from "../../constants/routes";

import "./Auth.css";

export default function ResetPassword() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") || "";

  const [showPassword, setShowPassword] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState("");

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({ defaultValues: { password: "", confirmPassword: "" } });

  const password = watch("password");

  const onSubmit = async (values) => {
    setServerError("");

    if (!token) {
      setServerError("This reset link is missing its token. Please request a new one.");
      return;
    }

    setSubmitting(true);

    try {
      await resetPassword({ token, newPassword: values.password });
      toast.success("Password updated. You can sign in now.");
      navigate(ROUTES.LOGIN, { replace: true });
    } catch (error) {
      const message =
        error?.response?.data?.message ||
        "This reset link is invalid or has expired. Please request a new one.";
      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout>
      <div className="auth-form-header">
        <h2>Set a new password</h2>
        <p>Choose a new password for your account.</p>
      </div>

      {serverError && (
        <div className="auth-alert">
          <FiAlertCircle size={16} style={{ flexShrink: 0, marginTop: 1 }} />
          <span>{serverError}</span>
        </div>
      )}

      <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
        <Field label="New password">
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiLock size={16} />
            </span>

            <input
              type={showPassword ? "text" : "password"}
              className={`input${errors.password ? " input-error" : ""}`}
              placeholder="Enter a new password"
              autoComplete="new-password"
              style={{ paddingRight: 44 }}
              {...register("password", {
                required: "Password is required",
                minLength: { value: 8, message: "Use at least 8 characters" },
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

        <Field label="Confirm new password">
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiLock size={16} />
            </span>

            <input
              type={showPassword ? "text" : "password"}
              className={`input${errors.confirmPassword ? " input-error" : ""}`}
              placeholder="Re-enter the new password"
              autoComplete="new-password"
              {...register("confirmPassword", {
                required: "Please confirm your password",
                validate: (value) => value === password || "Passwords do not match",
              })}
            />
          </div>
          {errors.confirmPassword && (
            <span className="field-error">{errors.confirmPassword.message}</span>
          )}
        </Field>

        <Button type="submit" size="lg" className="auth-submit" disabled={submitting}>
          {submitting ? "Updating..." : "Update password"}
        </Button>
      </form>

      <p className="auth-form-footer">
        Remembered your password?{" "}
        <Link to={ROUTES.LOGIN} className="auth-link">
          Back to login
        </Link>
      </p>
    </AuthLayout>
  );
}
