import "./AppointmentPage.css";
import { ChevronLeftIcon, ChevronRightIcon, PlusIcon } from "@heroicons/react/24/outline";

export default function AppointmentToolbar() {
  return (
    <div className="flex-between" style={{ marginBottom: 24 }}>
      <div className="flex-row" style={{ gap: 16 }}>
        <button className="appt-date-btn panel" style={{ width: 44, height: 44, justifyContent: "center" }}>
          <ChevronLeftIcon width={20} height={20} />
        </button>

        <div className="panel appt-date-label" style={{ padding: "12px 24px" }}>
          Thursday, 06 August
        </div>

        <button className="appt-date-btn panel" style={{ width: 44, height: 44, justifyContent: "center" }}>
          <ChevronRightIcon width={20} height={20} />
        </button>
      </div>

      <button className="btn btn-primary btn-md">
        <PlusIcon width={20} height={20} />
        New Visit
      </button>
    </div>
  );
}
