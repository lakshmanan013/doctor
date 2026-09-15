import { Outlet } from "react-router-dom";

import Sidebar from "../components/common/Sidebar";
import Navbar from "../components/common/Navbar";

export default function DashboardLayout() {
  return (
    <div className="app-shell">
      <Sidebar />

      <div className="app-main">
        <Navbar />

        <main className="app-content">
          <div className="app-content-inner">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
