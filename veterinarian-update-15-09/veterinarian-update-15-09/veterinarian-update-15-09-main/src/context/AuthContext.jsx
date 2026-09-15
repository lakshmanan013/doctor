import { createContext, useCallback, useContext, useEffect, useState } from "react";

import { login as loginRequest, register as registerRequest } from "../services/authService";

const TOKEN_KEY = "zenve_doctor_token";
const USER_KEY = "zenve_doctor_user";

const AuthContext = createContext(null);

function readStoredToken() {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token || token === "zenve_demo_token") {
    localStorage.removeItem(TOKEN_KEY);
    return null;
  }
  return token;
}

function readStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(readStoredToken);
  const [doctor, setDoctor] = useState(readStoredUser);
  const [initializing, setInitializing] = useState(false);

  const persistSession = useCallback((data) => {
    const { token: newToken, ...doctorInfo } = data || {};

    if (newToken) {
      localStorage.setItem(TOKEN_KEY, newToken);
      setToken(newToken);
    }

    localStorage.setItem(USER_KEY, JSON.stringify(doctorInfo));
    setDoctor(doctorInfo);
  }, []);

  const login = useCallback(
    async (payload) => {
      const data = await loginRequest(payload);
      persistSession(data);
      return data;
    },
    [persistSession]
  );

  const register = useCallback(
    async (payload) => {
      const data = await registerRequest(payload);

      // A new doctor account starts PENDING admin approval and the backend
      // deliberately does not send a token back - only persist a session
      // when one is actually issued (i.e. an already-approved flow).
      if (data?.token) {
        persistSession(data);
      }

      return data;
    },
    [persistSession]
  );

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setToken(null);
    setDoctor(null);
  }, []);

  // Keep session state in sync if the token is cleared elsewhere
  // (e.g. a 401 response wipes it out inside the axios interceptor).
  useEffect(() => {
    const syncFromStorage = () => {
      setToken(readStoredToken());
      setDoctor(readStoredUser());
    };

    window.addEventListener("storage", syncFromStorage);
    return () => window.removeEventListener("storage", syncFromStorage);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        doctor,
        token,
        isAuthenticated: Boolean(token),
        initializing,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuthContext() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuthContext must be used inside AuthProvider");
  }

  return context;
}
