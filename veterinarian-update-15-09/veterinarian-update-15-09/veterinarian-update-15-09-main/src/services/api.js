import axios from "axios";

const TOKEN_KEY = "zenve_doctor_token";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "/api",
  headers: { "Content-Type": "application/json" },
  timeout: 15000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);

  if (token && token !== "zenve_demo_token") {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API request failed:", error?.response?.data || error.message);

    if (error?.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem("zenve_doctor_user");

      if (
        !window.location.pathname.startsWith("/login") &&
        !window.location.pathname.startsWith("/register") &&
        !window.location.pathname.startsWith("/forgot-password") &&
        !window.location.pathname.startsWith("/reset-password")
      ) {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default api;
