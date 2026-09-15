import { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { FiMail, FiLock, FiEye, FiEyeOff, FiAlertCircle } from "react-icons/fi";

import Button from "../../components/ui/Button";
import { Field } from "../../components/ui/Input";
import AuthLayout from "../../layouts/AuthLayout";
import { forgotPassword, verifyOtp, resetPassword } from "../../services/authService";
import { ROUTES } from "../../constants/routes";

import "./Auth.css";

const OTP_LENGTH = 6;
const EMPTY_OTP = Array(OTP_LENGTH).fill("");

export default function ForgotPassword() {
  const navigate = useNavigate();

  // 1 = enter email, 2 = enter OTP, 3 = set new password
  const [step, setStep] = useState(1);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState("");

  const [email, setEmail] = useState("");
  const [resetToken, setResetToken] = useState("");
  const [otp, setOtp] = useState(EMPTY_OTP);
  const otpRefs = useRef([]);

  const emailForm = useForm({ defaultValues: { email: "" } });

  const passwordForm = useForm({
    defaultValues: { password: "", confirmPassword: "" },
  });
  const newPassword = passwordForm.watch("password");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const extractErrorMessage = (error, fallback) =>
    error?.response?.data?.message || fallback;

  // =====================================================
  // STEP 1 — email -> send OTP
  // =====================================================

  const handleSendOtp = async (values) => {
    const cleanEmail = values.email.trim().toLowerCase();
    setServerError("");
    setSubmitting(true);

    try {
      const data = await forgotPassword({ email: cleanEmail });

      setEmail(cleanEmail);
      setResetToken(data.resetToken);
      setOtp(EMPTY_OTP);
      toast.success(data.message || "OTP sent to your email.");
      setStep(2);
    } catch (error) {
      const message = extractErrorMessage(error, "Could not send OTP. Please try again.");
      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  // =====================================================
  // STEP 2 — OTP input helpers
  // =====================================================

  const handleOtpChange = (value, index) => {
    if (!/^\d*$/.test(value)) return;

    const next = [...otp];
    next[index] = value.slice(-1);
    setOtp(next);

    if (value && index < OTP_LENGTH - 1) {
      otpRefs.current[index + 1]?.focus();
    }
  };

  const handleOtpKeyDown = (e, index) => {
    if (e.key === "Backspace" && !otp[index] && index > 0) {
      otpRefs.current[index - 1]?.focus();
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    const enteredOtp = otp.join("");

    if (enteredOtp.length !== OTP_LENGTH) {
      setServerError("Please enter the complete 6-digit OTP.");
      return;
    }

    setServerError("");
    setSubmitting(true);

    try {
      const data = await verifyOtp({ email, otp: enteredOtp, resetToken });

      setResetToken(data.resetToken || resetToken);
      toast.success(data.message || "OTP verified.");
      setStep(3);
    } catch (error) {
      const message = extractErrorMessage(error, "Invalid or expired OTP.");
      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleResendOtp = async () => {
    setServerError("");
    setSubmitting(true);

    try {
      const data = await forgotPassword({ email });

      setResetToken(data.resetToken);
      setOtp(EMPTY_OTP);
      otpRefs.current[0]?.focus();
      toast.success(data.message || "A new OTP has been sent.");
    } catch (error) {
      const message = extractErrorMessage(error, "Unable to resend OTP.");
      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleChangeEmail = () => {
    setStep(1);
    setOtp(EMPTY_OTP);
    setResetToken("");
    setServerError("");
  };

  // =====================================================
  // STEP 3 — set new password
  // =====================================================

  const handleResetPassword = async (values) => {
    setServerError("");
    setSubmitting(true);

    try {
      const data = await resetPassword({
        email,
        resetToken,
        newPassword: values.password,
      });

      toast.success(data.message || "Password updated. You can sign in now.");
      navigate(ROUTES.LOGIN, { replace: true });
    } catch (error) {
      const message = extractErrorMessage(
        error,
        "Could not reset your password. Please start over."
      );
      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  // =====================================================
  // UI
  // =====================================================

  const ErrorAlert = () =>
    serverError ? (
      <div className="auth-alert" style={{ marginBottom: 18 }}>
        <FiAlertCircle size={16} style={{ flexShrink: 0, marginTop: 1 }} />
        <span>{serverError}</span>
      </div>
    ) : null;

  if (step === 1) {
    return (
      <AuthLayout>
        <div className="auth-form-header">
          <h2>Forgot password</h2>
          <p>Enter your account email and we&apos;ll send you a 6-digit OTP.</p>
        </div>

        <ErrorAlert />

        <form
          className="auth-form"
          onSubmit={emailForm.handleSubmit(handleSendOtp)}
          noValidate
        >
          <Field label="Email address">
            <div className="auth-input-wrap">
              <span className="auth-input-icon">
                <FiMail size={16} />
              </span>

              <input
                type="email"
                className={`input${emailForm.formState.errors.email ? " input-error" : ""}`}
                placeholder="you@clinic.com"
                autoComplete="email"
                {...emailForm.register("email", {
                  required: "Email is required",
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: "Enter a valid email address",
                  },
                })}
              />
            </div>
            {emailForm.formState.errors.email && (
              <span className="field-error">{emailForm.formState.errors.email.message}</span>
            )}
          </Field>

          <Button type="submit" size="lg" className="auth-submit" disabled={submitting}>
            {submitting ? "Sending..." : "Send OTP"}
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

  if (step === 2) {
    return (
      <AuthLayout>
        <div className="auth-form-header">
          <h2>Verify OTP</h2>
          <p>
            Enter the 6-digit code sent to <strong>{email}</strong>
          </p>
        </div>

        <ErrorAlert />

        <form className="auth-form" onSubmit={handleVerifyOtp} noValidate>
          <div className="otp-inputs">
            {otp.map((digit, index) => (
              <input
                key={index}
                ref={(el) => (otpRefs.current[index] = el)}
                type="text"
                inputMode="numeric"
                maxLength={1}
                value={digit}
                onChange={(e) => handleOtpChange(e.target.value, index)}
                onKeyDown={(e) => handleOtpKeyDown(e, index)}
                className="otp-box"
                disabled={submitting}
                autoFocus={index === 0}
              />
            ))}
          </div>

          <Button type="submit" size="lg" className="auth-submit" disabled={submitting}>
            {submitting ? "Verifying..." : "Verify OTP"}
          </Button>
        </form>

        <div className="auth-otp-actions">
          <button
            type="button"
            className="auth-link auth-link-btn"
            onClick={handleResendOtp}
            disabled={submitting}
          >
            Resend OTP
          </button>

          <button
            type="button"
            className="auth-link auth-link-btn"
            onClick={handleChangeEmail}
            disabled={submitting}
          >
            Change email
          </button>
        </div>

        <p className="auth-form-footer">
          Remembered your password?{" "}
          <Link to={ROUTES.LOGIN} className="auth-link">
            Back to login
          </Link>
        </p>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout>
      <div className="auth-form-header">
        <h2>Create new password</h2>
        <p>Choose a new password for your account.</p>
      </div>

      <ErrorAlert />

      <form
        className="auth-form"
        onSubmit={passwordForm.handleSubmit(handleResetPassword)}
        noValidate
      >
        <Field label="New password">
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiLock size={16} />
            </span>

            <input
              type={showPassword ? "text" : "password"}
              className={`input${passwordForm.formState.errors.password ? " input-error" : ""}`}
              placeholder="Enter a new password"
              autoComplete="new-password"
              style={{ paddingRight: 44 }}
              {...passwordForm.register("password", {
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
          {passwordForm.formState.errors.password && (
            <span className="field-error">
              {passwordForm.formState.errors.password.message}
            </span>
          )}
        </Field>

        <Field label="Confirm new password">
          <div className="auth-input-wrap">
            <span className="auth-input-icon">
              <FiLock size={16} />
            </span>

            <input
              type={showConfirmPassword ? "text" : "password"}
              className={`input${
                passwordForm.formState.errors.confirmPassword ? " input-error" : ""
              }`}
              placeholder="Re-enter the new password"
              autoComplete="new-password"
              style={{ paddingRight: 44 }}
              {...passwordForm.register("confirmPassword", {
                required: "Please confirm your password",
                validate: (value) => value === newPassword || "Passwords do not match",
              })}
            />

            <button
              type="button"
              className="auth-input-toggle"
              onClick={() => setShowConfirmPassword((prev) => !prev)}
              tabIndex={-1}
              aria-label={showConfirmPassword ? "Hide password" : "Show password"}
            >
              {showConfirmPassword ? <FiEyeOff size={16} /> : <FiEye size={16} />}
            </button>
          </div>
          {passwordForm.formState.errors.confirmPassword && (
            <span className="field-error">
              {passwordForm.formState.errors.confirmPassword.message}
            </span>
          )}
        </Field>

        <Button type="submit" size="lg" className="auth-submit" disabled={submitting}>
          {submitting ? "Updating..." : "Reset password"}
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
