import api from "./api";

// Backed by doctor.backend.controller.AuthController.
export const login = async (payload) => (await api.post("/auth/login", payload)).data;
export const register = async (payload) => (await api.post("/auth/register", payload)).data;
export const getCurrentUser = async () => (await api.get("/auth/me")).data;

// OTP-based forgot-password flow (see doctor.backend.controller.AuthController):
//   1. forgotPassword({ email })              -> emails a 6-digit OTP, returns { message, resetToken }
//   2. verifyOtp({ email, otp, resetToken })   -> returns { verified, message, resetToken } (a NEW token)
//   3. resetPassword({ email, resetToken, newPassword }) -> only accepts the token from step 2
export const forgotPassword = async (payload) => (await api.post("/auth/forgot-password", payload)).data;
export const verifyOtp = async (payload) => (await api.post("/auth/verify-otp", payload)).data;
export const resetPassword = async (payload) => (await api.post("/auth/reset-password", payload)).data;
