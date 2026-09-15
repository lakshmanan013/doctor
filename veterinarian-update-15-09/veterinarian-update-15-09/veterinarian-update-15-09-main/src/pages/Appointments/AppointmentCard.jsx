import "./AppointmentPage.css";
import StatusBadge from "./StatusBadge";
import { UserIcon, PhoneIcon } from "@heroicons/react/24/outline";

const BORDER_TONE = {
  Completed: "tone-success",
  Consultation: "tone-info",
  Waiting: "tone-primary",
  Confirmed: "tone-warning",
  Emergency: "tone-danger",
};

export default function AppointmentCard({ appointment }) {
  return (
    <div className={`appt-card ${BORDER_TONE[appointment.status] || ""}`}>
      <div className="flex-between">
        <div className="appt-card-left">
          <div className="row-avatar appt-card-avatar">{appointment.icon}</div>

          <div>
            <h2 className="appt-card-name">
              {appointment.petName}
              <span className="text-faint appt-card-meta"> • {appointment.breed}</span>
              <span className="text-muted appt-card-meta"> • {appointment.owner}</span>
            </h2>

            <div className="appt-card-info">
              <div className="flex-row" style={{ gap: 8 }}>
                <PhoneIcon width={16} height={16} />
                {appointment.type}
              </div>
              <div className="flex-row" style={{ gap: 8 }}>
                <UserIcon width={16} height={16} />
                {appointment.reason}
              </div>
            </div>
          </div>
        </div>

        <StatusBadge status={appointment.status} />
      </div>
    </div>
  );
}
