import { useAuthContext } from "../context/AuthContext";

// Thin re-export so pages can `import { useAuth } from "../../hooks/useAuth"`
// without knowing the state lives in AuthContext.
export function useAuth() {
  return useAuthContext();
}

export default useAuth;
