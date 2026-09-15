import { Navigate } from "react-router-dom";

import { useAuth } from "../hooks/useAuth";
import { ROUTES } from "../constants/routes";

function PublicRoute({ children }) {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={ROUTES.DASHBOARD} replace />;
  }

  return children;
}

export default PublicRoute;
