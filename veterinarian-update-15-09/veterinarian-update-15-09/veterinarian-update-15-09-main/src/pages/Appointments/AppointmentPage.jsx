import { useEffect, useMemo, useState } from "react";
import { FiChevronLeft, FiChevronRight, FiPlus, FiVideo, FiMapPin, FiAlertTriangle, FiTrash2, FiEdit2 } from "react-icons/fi";
import { FaStethoscope } from "react-icons/fa";
import toast from "react-hot-toast";
import Button from "../../components/ui/Button";
import Badge from "../../components/ui/Badge";
import NewVisitModal from "../../components/modals/NewVisitModal";
import { getAppointments, getAppointmentsByDate, deleteAppointment } from "../../services/appointmentService";
import "./AppointmentPage.css";

const FILTERS = ["All", "In Clinic", "Video", "Home Visit", "Emergency"];
const STATUS_VARIANT = { Completed: "success", "In Consultation": "info", Waiting: "warning", Confirmed: "navy", Cancelled: "danger" };
const TYPE_ICON = { "In Clinic": FaStethoscope, Video: FiVideo, "Home Visit": FiMapPin, Emergency: FiAlertTriangle };

const iso = (d) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
const labelDate = (d) => d.toLocaleDateString("en-IN", { weekday: "long", day: "numeric", month: "long" });

export default function AppointmentPage() {
  const [date, setDate] = useState(new Date());
  const [appointments, setAppointments] = useState([]);
  const [filter, setFilter] = useState("All");
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const load = async () => { try { const data = await getAppointmentsByDate(iso(date)); setAppointments(Array.isArray(data) ? data : []); } catch { const data = await getAppointments().catch(() => []); setAppointments((data || []).filter((a) => a.appointmentDate === iso(date))); } };
  useEffect(() => { load(); }, [date]);
  const list = useMemo(() => filter === "All" ? appointments : appointments.filter((a) => a.appointmentType === filter), [appointments, filter]);
  const move = (days) => setDate((d) => { const x = new Date(d); x.setDate(x.getDate() + days); return x; });
  const remove = async (a) => { if (!window.confirm(`Delete visit for ${a.patientName}?`)) return; try { await deleteAppointment(a.id); toast.success("Appointment deleted"); load(); } catch (e) { toast.error(e?.response?.data?.message || "Could not delete appointment"); } };
  return <div className="stack-6">
    <div className="appt-toolbar"><div className="appt-date-nav"><button className="appt-date-btn" onClick={() => move(-1)}><FiChevronLeft size={18} /></button><span className="appt-date-label">{labelDate(date)}</span><button className="appt-date-btn" onClick={() => move(1)}><FiChevronRight size={18} /></button></div><Button icon={FiPlus} onClick={() => setOpen(true)}>New visit</Button></div>
    <div className="appt-filters">{FILTERS.map((f) => <button key={f} onClick={() => setFilter(f)} className={`appt-filter-btn ${filter === f ? "active" : ""}`}>{f}</button>)}</div>
    <div className="table-card">{list.map((a) => { const Icon = TYPE_ICON[a.appointmentType] || FaStethoscope; return <div key={a.id} className="appt-row"><div className="row-time" style={{ width: 96 }}>{a.appointmentTime || "—"}</div><div className="row-avatar" style={{ width: 40, height: 40 }}>{a.icon || "🐾"}</div><div className="row-body"><p className="row-title">{a.patientName || "Patient"} · {a.ownerName || ""}</p><p className="row-desc appt-row-type"><Icon size={12} /> {a.appointmentType || "Visit"} · {a.reason || ""}</p></div><Badge variant={STATUS_VARIANT[a.status] || "slate"}>{(a.status || "Scheduled").toUpperCase()}</Badge><button className="link-action" onClick={() => setEditing(a)} title="Edit"><FiEdit2 size={15} /></button><button className="link-action" onClick={() => remove(a)} title="Delete"><FiTrash2 size={15} /></button></div>; })}{!list.length && <p className="appt-empty">No visits for this date.</p>}</div>
    <NewVisitModal open={open} onClose={() => setOpen(false)} onCreated={load} />
    <NewVisitModal open={Boolean(editing)} appointment={editing} onClose={() => setEditing(null)} onUpdated={load} />
  </div>;
}
