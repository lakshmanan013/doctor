import { useEffect, useState } from "react";
import "./AppointmentPage.css";
import AppointmentCard from "./AppointmentCard";
import TimeSlot from "./TimeSlot";
import { getAppointments } from "../../services/appointmentService";

function AppointmentCalendar() {
  const [appointments, setAppointments] = useState([]);
  useEffect(() => { getAppointments().then((d) => setAppointments(Array.isArray(d) ? d : [])).catch(() => setAppointments([])); }, []);
  return <div className="stack-6">{appointments.map((a) => <div key={a.id} className="calendar-row"><TimeSlot time={a.appointmentTime || "—"} /><div style={{ flex: 1 }}><AppointmentCard appointment={{ ...a, pet: a.patientName, owner: a.ownerName, time: a.appointmentTime, type: a.appointmentType, reason: a.reason, icon: a.icon || "🐾" }} /></div></div>)}</div>;
}
export default AppointmentCalendar;
