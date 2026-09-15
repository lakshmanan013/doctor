import { useEffect, useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import {
  Calendar,
  Users,
  FileText,
  Syringe,
  IndianRupee,
  Activity,
  Clock,
  CheckCircle2,
  Stethoscope,
  ClipboardCheck,
  Building2,
  Home,
  Video,
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
  MoreVertical,
  ArrowRight,
  ArrowUpRight,
} from "lucide-react";

import { getAppointments } from "../../services/appointmentService";
import { getPatients } from "../../services/patientService";
import { getPrescriptions } from "../../services/prescriptionService";
import { getInvoices } from "../../services/billingService";
import { getVaccinations } from "../../services/vaccinationService";
import "./Dashboard.css";

const SPECIES_ICONS = {
  Dog: "🐶",
  Cat: "🐱",
  Buffalo: "🐃",
  Goat: "🐐",
  Cattle: "🐄",
  Rabbit: "🐰",
  Parrot: "🦜",
  Bird: "🦜",
  Other: "🐾",
};

function formatRelativeTime(dateStr) {
  if (!dateStr) return "Recently";
  const date = new Date(dateStr);
  if (Number.isNaN(date.getTime())) return String(dateStr).slice(0, 10);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  if (diffMs < 0) {
    return date.toLocaleDateString("en-IN", { day: "numeric", month: "short" });
  }
  const diffMins = Math.floor(diffMs / 60000);
  if (diffMins < 1) return "Just now";
  if (diffMins < 60) return `${diffMins} min ago`;
  const diffHours = Math.floor(diffMins / 60);
  if (diffHours < 24) return `${diffHours} hr${diffHours > 1 ? "s" : ""} ago`;
  const diffDays = Math.floor(diffHours / 24);
  if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? "s" : ""} ago`;
  return date.toLocaleDateString("en-IN", { day: "numeric", month: "short" });
}

export default function Dashboard() {
  const navigate = useNavigate();

  const [appointments, setAppointments] = useState([]);
  const [patients, setPatients] = useState([]);
  const [prescriptions, setPrescriptions] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [vaccinations, setVaccinations] = useState([]);
  const [loading, setLoading] = useState(true);

  const getTodayLocalIso = () => {
    const d = new Date();
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
  };

  const [todayIso, setTodayIso] = useState(getTodayLocalIso);
  const [selectedDate, setSelectedDate] = useState(getTodayLocalIso);
  const [hoveredTrendIso, setHoveredTrendIso] = useState(null);
  const [calendarMonth, setCalendarMonth] = useState(() => new Date());

  // Automatically update date when day rolls over
  useEffect(() => {
    const timer = setInterval(() => {
      const currentIso = getTodayLocalIso();
      setTodayIso((prev) => {
        if (prev !== currentIso) {
          setSelectedDate((sel) => (sel === prev ? currentIso : sel));
          return currentIso;
        }
        return prev;
      });
    }, 30000);
    return () => clearInterval(timer);
  }, []);

  // ========================================================
  // 1. DATA LOADING FROM DATABASE APIS
  // ========================================================
  const loadAllData = async () => {
    try {
      const [appts, pts, rxs, invs, vacs] = await Promise.all([
        getAppointments().catch(() => []),
        getPatients().catch(() => []),
        getPrescriptions().catch(() => []),
        getInvoices().catch(() => []),
        getVaccinations().catch(() => []),
      ]);

      const safeAppts = Array.isArray(appts) ? appts : [];
      setAppointments(safeAppts);
      setPatients(Array.isArray(pts) ? pts : []);
      setPrescriptions(Array.isArray(rxs) ? rxs : []);
      setInvoices(Array.isArray(invs) ? invs : []);
      setVaccinations(Array.isArray(vacs) ? vacs : []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAllData();

    // Listen for events from other pages / modals
    const handleRefresh = () => loadAllData();
    window.addEventListener("appointmentsUpdated", handleRefresh);
    window.addEventListener("patientsUpdated", handleRefresh);
    window.addEventListener("prescriptionsUpdated", handleRefresh);
    window.addEventListener("invoicesUpdated", handleRefresh);
    window.addEventListener("vaccinationsUpdated", handleRefresh);

    return () => {
      window.removeEventListener("appointmentsUpdated", handleRefresh);
      window.removeEventListener("patientsUpdated", handleRefresh);
      window.removeEventListener("prescriptionsUpdated", handleRefresh);
      window.removeEventListener("invoicesUpdated", handleRefresh);
      window.removeEventListener("vaccinationsUpdated", handleRefresh);
    };
  }, [todayIso]);

  // ========================================================
  // 2. METRICS & COUNTS (PURE DATABASE DATA)
  // ========================================================
  const todayAppts = useMemo(
    () => appointments.filter((a) => a.appointmentDate === todayIso),
    [appointments, todayIso]
  );

  const activeAppts = useMemo(() => {
    const filtered = appointments.filter((a) => a.appointmentDate === selectedDate);
    if (filtered.length > 0) return filtered;
    return todayAppts;
  }, [appointments, selectedDate, todayAppts]);

  const apptsCount = todayAppts.length;
  const patientsCount = patients.length;
  const prescriptionsCount = prescriptions.length;
  const vaccinationsCount = vaccinations.length;

  // Invoices for today vs all-time
  const todayInvoices = useMemo(
    () => invoices.filter((i) => i.invoiceDate === todayIso),
    [invoices, todayIso]
  );
  const revenueTodayNum = useMemo(
    () => todayInvoices.reduce((sum, i) => sum + Number(i.paidAmount || 0), 0),
    [todayInvoices]
  );

  const allRevenueCollected = useMemo(
    () => invoices.reduce((sum, i) => sum + Number(i.paidAmount || 0), 0),
    [invoices]
  );
  const allRevenueDue = useMemo(
    () => invoices.reduce((sum, i) => sum + Math.max(Number(i.dueAmount || 0), 0), 0),
    [invoices]
  );
  const allRevenueTotal = allRevenueCollected + allRevenueDue;

  // Selected date or today's collection
  const selectedDateInvoices = useMemo(() => {
    const onDate = invoices.filter((i) => i.invoiceDate === selectedDate);
    if (onDate.length > 0) return onDate;
    if (todayInvoices.length > 0) return todayInvoices;
    return invoices;
  }, [invoices, selectedDate, todayInvoices]);

  const selectedCollectedNum = useMemo(
    () => selectedDateInvoices.reduce((sum, i) => sum + Number(i.paidAmount || 0), 0),
    [selectedDateInvoices]
  );
  const selectedDueNum = useMemo(
    () => selectedDateInvoices.reduce((sum, i) => sum + Math.max(Number(i.dueAmount || 0), 0), 0),
    [selectedDateInvoices]
  );
  const selectedTotalNum = selectedCollectedNum + selectedDueNum;
  const collectionRate = selectedTotalNum > 0
    ? Math.round((selectedCollectedNum / selectedTotalNum) * 100)
    : 0;

  // ========================================================
  // 3. APPOINTMENTS TREND (DYNAMIC DATABASE CHART)
  // ========================================================
  const trendDays = useMemo(() => {
    const datesWithAppts = appointments.map((a) => a.appointmentDate).filter(Boolean);
    const maxApptDate = datesWithAppts.length > 0 ? [...datesWithAppts].sort().reverse()[0] : todayIso;
    const endDateStr = maxApptDate > todayIso ? maxApptDate : todayIso;
    const [y, m, d] = endDateStr.split("-").map(Number);
    const refDate = new Date(y, m - 1, d);

    const days = [];
    for (let i = 4; i >= 0; i--) {
      const dayDate = new Date(refDate);
      dayDate.setDate(dayDate.getDate() - i);
      const dy = dayDate.getFullYear();
      const dm = String(dayDate.getMonth() + 1).padStart(2, "0");
      const dd = String(dayDate.getDate()).padStart(2, "0");
      const iso = `${dy}-${dm}-${dd}`;
      const count = appointments.filter((a) => a.appointmentDate === iso).length;
      const label = dayDate.toLocaleDateString("en-IN", { day: "numeric", month: "short" });
      days.push({ iso, label, count });
    }
    return days;
  }, [appointments, todayIso]);

  // Clean, evenly spaced Y-axis grid scale
  const { trendMax, trendTicks } = useMemo(() => {
    const counts = trendDays.map((d) => d.count);
    const peak = Math.max(...counts, 0);

    // Pick a step size that produces 4 equal integer intervals
    const stepOptions = [1, 2, 5, 10, 15, 20, 25, 50, 100];
    const numIntervals = 4;
    const targetMin = Math.max(peak + (peak >= 4 ? 1 : 0), 4);
    const step = stepOptions.find((s) => s * numIntervals >= targetMin) || 10;
    const maxVal = step * numIntervals;
    const ticks = [];
    for (let i = numIntervals; i >= 0; i--) {
      ticks.push(step * i);
    }
    return { trendMax: maxVal, trendTicks: ticks };
  }, [trendDays]);

  const chartPoints = useMemo(() => {
    const xCoords = [68, 176, 284, 392, 500];
    return trendDays.map((td, index) => {
      const x = xCoords[index] || 68 + index * 108;
      // y ranges from 155 (count=0) down to 28 (count=trendMax)
      const ratio = trendMax > 0 ? td.count / trendMax : 0;
      const y = Math.round(155 - ratio * 127);
      return { ...td, x, y };
    });
  }, [trendDays, trendMax]);

  const trendPathD = useMemo(() => {
    if (chartPoints.length < 2) return "";
    let d = `M ${chartPoints[0].x},${chartPoints[0].y}`;
    for (let i = 0; i < chartPoints.length - 1; i++) {
      const p0 = chartPoints[i];
      const p1 = chartPoints[i + 1];
      const cx = (p0.x + p1.x) / 2;
      d += ` C ${cx},${p0.y} ${cx},${p1.y} ${p1.x},${p1.y}`;
    }
    return d;
  }, [chartPoints]);

  const trendAreaD = useMemo(() => {
    if (!trendPathD || chartPoints.length < 2) return "";
    const first = chartPoints[0];
    const last = chartPoints[chartPoints.length - 1];
    return `${trendPathD} L ${last.x},155 L ${first.x},155 Z`;
  }, [trendPathD, chartPoints]);

  const activeTrendPoint = useMemo(() => {
    const targetIso = hoveredTrendIso || selectedDate;
    const match = chartPoints.find((p) => p.iso === targetIso);
    return match || chartPoints[chartPoints.length - 1] || { x: 500, y: 58, count: 0, label: "" };
  }, [chartPoints, hoveredTrendIso, selectedDate]);

  // ========================================================
  // 4. DONUT PANELS (DATABASE-COMPUTED)
  // ========================================================
  const appointmentsCompleted = activeAppts.filter(
    (a) => String(a.status).toLowerCase() === "completed"
  ).length;

  const appointmentsCancelled = activeAppts.filter(
    (a) => String(a.status).toLowerCase() === "cancelled"
  ).length;

  const appointmentsTotal = activeAppts.length;

  // Scheduled / Active appointments (waiting, confirmed, scheduled, in consultation, etc.)
  const appointmentsScheduled = Math.max(
    0,
    appointmentsTotal - appointmentsCompleted - appointmentsCancelled
  );

  const apptCompletedPct = appointmentsTotal > 0
    ? appointmentsCompleted / appointmentsTotal
    : 0;
  const apptScheduledPct = appointmentsTotal > 0
    ? appointmentsScheduled / appointmentsTotal
    : 0;
  const apptCancelledPct = appointmentsTotal > 0
    ? appointmentsCancelled / appointmentsTotal
    : 0;

  // Billing Overview Donut
  const billingCollectedPct = allRevenueTotal > 0
    ? allRevenueCollected / allRevenueTotal
    : 0;
  const billingDuePct = allRevenueTotal > 0
    ? allRevenueDue / allRevenueTotal
    : 0;

  // ========================================================
  // 5. STATUS & VISIT TYPE COUNTS (DATABASE-COMPUTED)
  // ========================================================
  const waitingCount = activeAppts.filter(
    (a) => String(a.status).toLowerCase() === "waiting"
  ).length;
  const confirmedCount = activeAppts.filter((a) => {
    const s = String(a.status).toLowerCase();
    return s === "confirmed" || s === "scheduled";
  }).length;
  const inConsultationCount = activeAppts.filter(
    (a) => String(a.status).toLowerCase() === "in consultation"
  ).length;
  const completedCount = activeAppts.filter(
    (a) => String(a.status).toLowerCase() === "completed"
  ).length;

  const inClinicCount = activeAppts.filter(
    (a) => String(a.appointmentType).toLowerCase() === "in clinic"
  ).length;
  const homeVisitCount = activeAppts.filter(
    (a) => String(a.appointmentType).toLowerCase() === "home visit"
  ).length;
  const videoCount = activeAppts.filter(
    (a) => String(a.appointmentType).toLowerCase() === "video"
  ).length;
  const emergencyCount = activeAppts.filter(
    (a) => String(a.appointmentType).toLowerCase() === "emergency"
  ).length;

  // ========================================================
  // 6. UPCOMING APPOINTMENTS (FROM DATABASE)
  // ========================================================
  const upcomingAppointments = useMemo(() => {
    return appointments
      .filter((a) => a.appointmentDate >= todayIso && String(a.status).toLowerCase() !== "cancelled")
      .sort((a, b) => (a.appointmentDate || "").localeCompare(b.appointmentDate || "") || (a.appointmentTime || "").localeCompare(b.appointmentTime || ""))
      .slice(0, 4);
  }, [appointments, todayIso]);

  // ========================================================
  // 7. DYNAMIC CALENDAR GENERATION
  // ========================================================
  const { monthLabel, calendarDays } = useMemo(() => {
    const yr = calendarMonth.getFullYear();
    const mo = calendarMonth.getMonth();
    const label = calendarMonth.toLocaleDateString("en-IN", { month: "long", year: "numeric" });

    const firstDayIndex = new Date(yr, mo, 1).getDay();
    const daysInCurrent = new Date(yr, mo + 1, 0).getDate();
    const daysInPrev = new Date(yr, mo, 0).getDate();

    const cells = [];

    // Previous month filler
    for (let i = firstDayIndex - 1; i >= 0; i--) {
      const day = daysInPrev - i;
      const prevDate = new Date(yr, mo - 1, day);
      const pYr = prevDate.getFullYear();
      const pMo = String(prevDate.getMonth() + 1).padStart(2, "0");
      const pDay = String(day).padStart(2, "0");
      const iso = `${pYr}-${pMo}-${pDay}`;
      const hasDot = appointments.some((a) => a.appointmentDate === iso);
      cells.push({ day, isOtherMonth: true, iso, hasDot });
    }

    // Current month days
    for (let d = 1; d <= daysInCurrent; d++) {
      const cMo = String(mo + 1).padStart(2, "0");
      const cDay = String(d).padStart(2, "0");
      const iso = `${yr}-${cMo}-${cDay}`;
      const hasDot = appointments.some((a) => a.appointmentDate === iso);
      cells.push({ day: d, isOtherMonth: false, iso, hasDot });
    }

    // Next month filler
    const totalCells = cells.length;
    const targetCells = totalCells <= 35 ? 35 : 42;
    const remaining = targetCells - totalCells;
    for (let d = 1; d <= remaining; d++) {
      const nextDate = new Date(yr, mo + 1, d);
      const nYr = nextDate.getFullYear();
      const nMo = String(nextDate.getMonth() + 1).padStart(2, "0");
      const nDay = String(d).padStart(2, "0");
      const iso = `${nYr}-${nMo}-${nDay}`;
      const hasDot = appointments.some((a) => a.appointmentDate === iso);
      cells.push({ day: d, isOtherMonth: true, iso, hasDot });
    }

    return { monthLabel: label, calendarDays: cells };
  }, [calendarMonth, appointments]);

  const handlePrevMonth = () => {
    setCalendarMonth(new Date(calendarMonth.getFullYear(), calendarMonth.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setCalendarMonth(new Date(calendarMonth.getFullYear(), calendarMonth.getMonth() + 1, 1));
  };

  // ========================================================
  // 8. RECENT ACTIVITY (REAL DATABASE EVENTS)
  // ========================================================
  const recentActivities = useMemo(() => {
    const list = [];

    // Real appointments
    appointments.slice(-8).forEach((a) => {
      list.push({
        id: `appt-${a.id}`,
        icon: Calendar,
        iconBg: "green",
        title: `Appointment ${a.status || "booked"} for ${a.patientName || "Patient"}`,
        sub: `${a.appointmentTime ? a.appointmentTime + " • " : ""}${a.appointmentType || "Visit"}${a.doctorName ? " • Dr. " + a.doctorName : ""}`,
        date: a.createdAt || a.appointmentDate || "",
      });
    });

    // Real invoices
    invoices.slice(-5).forEach((i) => {
      list.push({
        id: `inv-${i.id}`,
        icon: IndianRupee,
        iconBg: "orange",
        title: `Payment received of ₹${Number(i.paidAmount || 0).toLocaleString("en-IN")}`,
        sub: `Invoice #${i.invoiceNumber || i.id} • ${i.paymentStatus || i.status || "Paid"}`,
        date: i.createdAt || i.invoiceDate || "",
      });
    });

    // Real prescriptions
    prescriptions.slice(-5).forEach((p) => {
      list.push({
        id: `rx-${p.id}`,
        icon: FileText,
        iconBg: "blue",
        title: `Prescription created for ${p.patientName || "Patient"}`,
        sub: `Rx #${p.prescriptionNumber || p.id}${p.diagnosis ? " • " + p.diagnosis : ""}`,
        date: p.createdAt || p.prescriptionDate || "",
      });
    });

    // Real vaccinations
    vaccinations.slice(-5).forEach((v) => {
      list.push({
        id: `vac-${v.id}`,
        icon: Syringe,
        iconBg: "purple",
        title: `Vaccination: ${v.vaccineName || "Administered"}`,
        sub: `Patient ${v.patientName || "Pet"} • ${v.status || "Completed"}`,
        date: v.createdAt || v.vaccinationDate || "",
      });
    });

    list.sort((a, b) => (b.date || "").localeCompare(a.date || ""));

    return list.slice(0, 4).map((item) => ({
      ...item,
      time: item.date ? formatRelativeTime(item.date) : "Recent",
    }));
  }, [appointments, invoices, prescriptions, vaccinations]);

  const selectedDateFormatted = useMemo(() => {
    if (!selectedDate) return "";
    const [y, m, d] = selectedDate.split("-").map(Number);
    if (!y || !m || !d) return selectedDate;
    return new Date(y, m - 1, d).toLocaleDateString("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  }, [selectedDate]);

  return (
    <div className="dashboard-container">
      {/* ========================================================
          1. TOP 5 METRIC STAT CARDS ROW
      ======================================================== */}
      <section className="top-stats-grid">
        {/* Stat 1: Appointments Today */}
        <div className="metric-card" onClick={() => navigate("/appointments")}>
          <div className="metric-header">
            <div className="metric-icon-box bg-purple">
              <Calendar size={18} color="#fff" />
            </div>
            <div className="metric-label-group">
              <span className="metric-title">Appointments Today</span>
              <span className="metric-value">{apptsCount}</span>
            </div>
          </div>

          <div className="metric-footer">
            <span className="metric-trend text-green">
              <ArrowUpRight size={13} strokeWidth={2.5} />
              {apptsCount > 0
                ? `${apptsCount} scheduled for today`
                : `${appointments.length} total in system`}
            </span>
          </div>

          {/* ECG Line at Card Bottom */}
          <div className="metric-ecg-box">
            <svg viewBox="0 0 240 20" preserveAspectRatio="none" className="ecg-svg">
              <defs>
                <linearGradient id="ecg-purple" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stopColor="#8B5CF6" stopOpacity="0.25" />
                  <stop offset="35%" stopColor="#8B5CF6" stopOpacity="0.8" />
                  <stop offset="55%" stopColor="#8B5CF6" stopOpacity="1" />
                  <stop offset="75%" stopColor="#8B5CF6" stopOpacity="0.8" />
                  <stop offset="100%" stopColor="#8B5CF6" stopOpacity="0.3" />
                </linearGradient>
              </defs>
              <line x1="0" y1="11" x2="240" y2="11" stroke="#8B5CF6" strokeWidth="1" strokeOpacity="0.15" />
              <path
                d="M 0,11 L 45,11 Q 52,8 58,11 L 63,14 L 69,2 L 75,19 L 79,11 Q 87,7 95,11 L 140,11 Q 147,8 153,11 L 158,14 L 164,2 L 170,19 L 174,11 Q 182,7 190,11 L 240,11"
                fill="none"
                stroke="url(#ecg-purple)"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
              <circle cx="69" cy="2" r="2" fill="#8B5CF6" />
              <circle cx="164" cy="2" r="2" fill="#8B5CF6" />
            </svg>
          </div>
        </div>

        {/* Stat 2: Total Patients */}
        <div className="metric-card" onClick={() => navigate("/patients")}>
          <div className="metric-header">
            <div className="metric-icon-box bg-blue">
              <Users size={18} color="#fff" />
            </div>
            <div className="metric-label-group">
              <span className="metric-title">Total Patients</span>
              <span className="metric-value">{patientsCount}</span>
            </div>
          </div>

          <div className="metric-footer">
            <span className="metric-trend text-green">
              <ArrowUpRight size={13} strokeWidth={2.5} /> {patientsCount} registered pets
            </span>
          </div>

          {/* ECG Line at Card Bottom */}
          <div className="metric-ecg-box">
            <svg viewBox="0 0 240 20" preserveAspectRatio="none" className="ecg-svg">
              <defs>
                <linearGradient id="ecg-blue" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stopColor="#3B82F6" stopOpacity="0.25" />
                  <stop offset="35%" stopColor="#3B82F6" stopOpacity="0.8" />
                  <stop offset="55%" stopColor="#3B82F6" stopOpacity="1" />
                  <stop offset="75%" stopColor="#3B82F6" stopOpacity="0.8" />
                  <stop offset="100%" stopColor="#3B82F6" stopOpacity="0.3" />
                </linearGradient>
              </defs>
              <line x1="0" y1="11" x2="240" y2="11" stroke="#3B82F6" strokeWidth="1" strokeOpacity="0.15" />
              <path
                d="M 0,11 L 45,11 Q 52,8 58,11 L 63,14 L 69,2 L 75,19 L 79,11 Q 87,7 95,11 L 140,11 Q 147,8 153,11 L 158,14 L 164,2 L 170,19 L 174,11 Q 182,7 190,11 L 240,11"
                fill="none"
                stroke="url(#ecg-blue)"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
              <circle cx="69" cy="2" r="2" fill="#3B82F6" />
              <circle cx="164" cy="2" r="2" fill="#3B82F6" />
            </svg>
          </div>
        </div>

        {/* Stat 3: Prescriptions */}
        <div className="metric-card" onClick={() => navigate("/prescriptions")}>
          <div className="metric-header">
            <div className="metric-icon-box bg-green">
              <FileText size={18} color="#fff" />
            </div>
            <div className="metric-label-group">
              <span className="metric-title">Prescriptions</span>
              <span className="metric-value">{prescriptionsCount}</span>
            </div>
          </div>

          <div className="metric-footer">
            <span className="metric-trend text-green">
              <ArrowUpRight size={13} strokeWidth={2.5} /> {prescriptionsCount} issued records
            </span>
          </div>

          {/* ECG Line at Card Bottom */}
          <div className="metric-ecg-box">
            <svg viewBox="0 0 240 20" preserveAspectRatio="none" className="ecg-svg">
              <defs>
                <linearGradient id="ecg-green" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stopColor="#10B981" stopOpacity="0.25" />
                  <stop offset="35%" stopColor="#10B981" stopOpacity="0.8" />
                  <stop offset="55%" stopColor="#10B981" stopOpacity="1" />
                  <stop offset="75%" stopColor="#10B981" stopOpacity="0.8" />
                  <stop offset="100%" stopColor="#10B981" stopOpacity="0.3" />
                </linearGradient>
              </defs>
              <line x1="0" y1="11" x2="240" y2="11" stroke="#10B981" strokeWidth="1" strokeOpacity="0.15" />
              <path
                d="M 0,11 L 45,11 Q 52,8 58,11 L 63,14 L 69,2 L 75,19 L 79,11 Q 87,7 95,11 L 140,11 Q 147,8 153,11 L 158,14 L 164,2 L 170,19 L 174,11 Q 182,7 190,11 L 240,11"
                fill="none"
                stroke="url(#ecg-green)"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
              <circle cx="69" cy="2" r="2" fill="#10B981" />
              <circle cx="164" cy="2" r="2" fill="#10B981" />
            </svg>
          </div>
        </div>

        {/* Stat 4: Vaccinations */}
        <div className="metric-card" onClick={() => navigate("/vaccinations")}>
          <div className="metric-header">
            <div className="metric-icon-box bg-orange">
              <Syringe size={18} color="#fff" />
            </div>
            <div className="metric-label-group">
              <span className="metric-title">Vaccinations</span>
              <span className="metric-value">{vaccinationsCount}</span>
            </div>
          </div>

          <div className="metric-footer">
            <span className="metric-trend text-green">
              <ArrowUpRight size={13} strokeWidth={2.5} /> {vaccinationsCount} tracked vaccines
            </span>
          </div>

          {/* ECG Line at Card Bottom */}
          <div className="metric-ecg-box">
            <svg viewBox="0 0 240 20" preserveAspectRatio="none" className="ecg-svg">
              <defs>
                <linearGradient id="ecg-orange" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stopColor="#F59E0B" stopOpacity="0.25" />
                  <stop offset="35%" stopColor="#F59E0B" stopOpacity="0.8" />
                  <stop offset="55%" stopColor="#F59E0B" stopOpacity="1" />
                  <stop offset="75%" stopColor="#F59E0B" stopOpacity="0.8" />
                  <stop offset="100%" stopColor="#F59E0B" stopOpacity="0.3" />
                </linearGradient>
              </defs>
              <line x1="0" y1="11" x2="240" y2="11" stroke="#F59E0B" strokeWidth="1" strokeOpacity="0.15" />
              <path
                d="M 0,11 L 45,11 Q 52,8 58,11 L 63,14 L 69,2 L 75,19 L 79,11 Q 87,7 95,11 L 140,11 Q 147,8 153,11 L 158,14 L 164,2 L 170,19 L 174,11 Q 182,7 190,11 L 240,11"
                fill="none"
                stroke="url(#ecg-orange)"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
              <circle cx="69" cy="2" r="2" fill="#F59E0B" />
              <circle cx="164" cy="2" r="2" fill="#F59E0B" />
            </svg>
          </div>
        </div>

        {/* Stat 5: Revenue (Today) */}
        <div className="metric-card" onClick={() => navigate("/billing")}>
          <div className="metric-header">
            <div className="metric-icon-box bg-pink">
              <IndianRupee size={18} color="#fff" />
            </div>
            <div className="metric-label-group">
              <span className="metric-title">Revenue (Today)</span>
              <span className="metric-value">₹{revenueTodayNum.toLocaleString("en-IN")}</span>
            </div>
          </div>

          <div className="metric-footer">
            <span className="metric-trend text-green">
              <ArrowUpRight size={13} strokeWidth={2.5} /> ₹{allRevenueCollected.toLocaleString("en-IN")} total collected
            </span>
          </div>

          {/* ECG Line at Card Bottom */}
          <div className="metric-ecg-box">
            <svg viewBox="0 0 240 20" preserveAspectRatio="none" className="ecg-svg">
              <defs>
                <linearGradient id="ecg-pink" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stopColor="#EC4899" stopOpacity="0.25" />
                  <stop offset="35%" stopColor="#EC4899" stopOpacity="0.8" />
                  <stop offset="55%" stopColor="#EC4899" stopOpacity="1" />
                  <stop offset="75%" stopColor="#EC4899" stopOpacity="0.8" />
                  <stop offset="100%" stopColor="#EC4899" stopOpacity="0.3" />
                </linearGradient>
              </defs>
              <line x1="0" y1="11" x2="240" y2="11" stroke="#EC4899" strokeWidth="1" strokeOpacity="0.15" />
              <path
                d="M 0,11 L 45,11 Q 52,8 58,11 L 63,14 L 69,2 L 75,19 L 79,11 Q 87,7 95,11 L 140,11 Q 147,8 153,11 L 158,14 L 164,2 L 170,19 L 174,11 Q 182,7 190,11 L 240,11"
                fill="none"
                stroke="url(#ecg-pink)"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
              <circle cx="69" cy="2" r="2" fill="#EC4899" />
              <circle cx="164" cy="2" r="2" fill="#EC4899" />
            </svg>
          </div>
        </div>
      </section>

      {/* ========================================================
          2. MIDDLE ROW: APPOINTMENTS TREND & COLLECTED TODAY
      ======================================================== */}
      <section className="middle-dashboard-row">
        {/* Left: Appointments Trend */}
        <div className="dashboard-card trend-card">
          <div className="card-header-flex">
            <div>
              <h3 className="card-heading">Appointments Trend</h3>
              <p className="card-subheading">
                Day-by-day appointments from database
              </p>
            </div>
            <div className="trend-dropdown-btn">
              <span>Past 5 Days</span>
            </div>
          </div>

          <div className="trend-chart-container">
            <svg viewBox="0 0 540 180" className="trend-chart-svg">
              <defs>
                <linearGradient id="purpleAreaGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#6366F1" stopOpacity="0.28" />
                  <stop offset="100%" stopColor="#6366F1" stopOpacity="0.01" />
                </linearGradient>
              </defs>

              {/* Grid Lines & Y Axis Labels */}
              {trendTicks.map((val) => {
                const ratio = trendMax > 0 ? val / trendMax : 0;
                const y = Math.round(155 - ratio * 127);
                return (
                  <g key={val}>
                    <text x="24" y={y + 4} className="chart-axis-text">
                      {val}
                    </text>
                    <line
                      x1="44"
                      y1={y}
                      x2="524"
                      y2={y}
                      stroke="#F1F3F9"
                      strokeWidth="1"
                      strokeDasharray={val === 0 ? "none" : "3 3"}
                    />
                  </g>
                );
              })}

              {/* Area Fill */}
              {trendAreaD && (
                <path d={trendAreaD} fill="url(#purpleAreaGrad)" />
              )}

              {/* Trend Curve Line */}
              {trendPathD && (
                <path
                  d={trendPathD}
                  fill="none"
                  stroke="#6366F1"
                  strokeWidth="3"
                  strokeLinecap="round"
                />
              )}

              {/* Data Points */}
              {chartPoints.map((pt) => {
                const isActive = pt.iso === activeTrendPoint.iso;
                return (
                  <g
                    key={pt.iso}
                    onClick={() => setSelectedDate(pt.iso)}
                    onMouseEnter={() => setHoveredTrendIso(pt.iso)}
                    onMouseLeave={() => setHoveredTrendIso(null)}
                    style={{ cursor: "pointer" }}
                  >
                    {/* Wide transparent hit target */}
                    <circle cx={pt.x} cy={pt.y} r={16} fill="transparent" />

                    <circle
                      cx={pt.x}
                      cy={pt.y}
                      r={isActive ? 6 : 4}
                      fill="#6366F1"
                      className={isActive ? "active-chart-point" : ""}
                    />
                    {isActive && <circle cx={pt.x} cy={pt.y} r={2.5} fill="#FFFFFF" />}
                    {!isActive && (
                      <text
                        x={pt.x}
                        y={pt.y - 10}
                        textAnchor="middle"
                        className="chart-point-text"
                      >
                        {pt.count}
                      </text>
                    )}
                  </g>
                );
              })}
            </svg>

            {/* Floating Tooltip above active date */}
            {activeTrendPoint && (
              <div
                className="active-chart-tooltip"
                style={{
                  left: `${(activeTrendPoint.x / 540) * 100}%`,
                  top: `${Math.max(6, activeTrendPoint.y - 48)}px`,
                  transform:
                    activeTrendPoint.x > 420
                      ? "translateX(-82%)"
                      : activeTrendPoint.x < 120
                      ? "translateX(-18%)"
                      : "translateX(-50%)",
                }}
              >
                <div className="tooltip-date">{activeTrendPoint.label}</div>
                <div className="tooltip-appts">
                  {activeTrendPoint.count} {activeTrendPoint.count === 1 ? "Appointment" : "Appointments"}
                </div>
              </div>
            )}

            {/* X-Axis Labels */}
            <div className="trend-x-axis">
              {chartPoints.map((pt) => {
                const isActive = pt.iso === activeTrendPoint.iso;
                return (
                  <span
                    key={pt.iso}
                    className={`trend-x-label ${isActive ? "active-date-label" : ""}`}
                    style={{
                      left: `${(pt.x / 540) * 100}%`,
                    }}
                    onClick={() => setSelectedDate(pt.iso)}
                    onMouseEnter={() => setHoveredTrendIso(pt.iso)}
                    onMouseLeave={() => setHoveredTrendIso(null)}
                  >
                    {pt.label}
                  </span>
                );
              })}
            </div>
          </div>
        </div>

        {/* Right: Collected Today / Selected Date */}
        <div className="dashboard-card collected-today-card">
          <div className="collected-content-split">
            {/* Left Portion */}
            <div className="collected-left">
              <span className="collected-tag">
                COLLECTED ({selectedDateFormatted})
              </span>
              <h2 className="collected-amount">₹{selectedCollectedNum.toLocaleString("en-IN")}</h2>
              <p className="collected-sub">
                ₹{selectedDueNum.toLocaleString("en-IN")} due ·{" "}
                <span className="view-billing-link" onClick={() => navigate("/billing")}>
                  view billing
                </span>
              </p>

              <div className="wallet-illustration-box">
                <img
                  src="/icons/collected-rupee-hologram.png"
                  alt="3D Gold Indian Rupee Hologram Pedestal"
                  className="wallet-image"
                  onError={(e) => { e.currentTarget.style.display = "none"; }}
                />
              </div>

              <button
                type="button"
                className="view-billing-btn"
                onClick={() => navigate("/billing")}
              >
                <span>View Billing</span>
                <ArrowRight size={15} />
              </button>
            </div>

            {/* Right Portion: Circular Gauge */}
            <div className="collected-right">
              <div className="gauge-wrap">
                <svg viewBox="0 0 100 100" className="gauge-svg">
                  <circle
                    cx="50"
                    cy="50"
                    r="40"
                    fill="none"
                    stroke="#E6FAF0"
                    strokeWidth="8"
                  />
                  <circle
                    cx="50"
                    cy="50"
                    r="40"
                    fill="none"
                    stroke="#10B981"
                    strokeWidth="8"
                    strokeDasharray={`${2 * Math.PI * 40 * (collectionRate / 100)} ${2 * Math.PI * 40}`}
                    strokeDashoffset="0"
                    strokeLinecap="round"
                    transform="rotate(-90 50 50)"
                  />
                </svg>
                <div className="gauge-center-text">
                  <span className="gauge-percent">{collectionRate}%</span>
                </div>
              </div>

              <div className="gauge-meta">
                <span className="gauge-title">Collection Rate</span>
                <span className="gauge-trend text-green">
                  {selectedTotalNum > 0
                    ? `₹${selectedTotalNum.toLocaleString("en-IN")} total billed`
                    : "No invoices for date"}
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ========================================================
          3. DONUT PANELS ROW: STATUS BREAKDOWN & BILLING OVERVIEW
      ======================================================== */}
      <section className="donut-panels-grid">
        {/* Donut 1: Appointments For Selected Date */}
        <div className="dashboard-card donut-card">
          <div className="card-header-flex">
            <div>
              <h3 className="card-heading">
                Appointments ({selectedDate === todayIso ? "Today" : selectedDateFormatted})
              </h3>
              <p className="card-subheading">Status breakdown</p>
            </div>
            <div className="card-header-icon-pill">
              <Activity size={18} className="text-muted-icon" />
            </div>
          </div>

          <div className="donut-body">
            <div className="donut-chart-container">
              <svg viewBox="0 0 100 100" className="donut-svg">
                {/* Clean base track ring */}
                <circle
                  cx="50"
                  cy="50"
                  r="38"
                  fill="none"
                  stroke="#F1F5F9"
                  strokeWidth="16"
                />
                {appointmentsTotal > 0 && (
                  <>
                    {/* Completed: Green arc */}
                    {apptCompletedPct > 0 && (
                      <circle
                        cx="50"
                        cy="50"
                        r="38"
                        fill="none"
                        stroke="#10B981"
                        strokeWidth="16"
                        strokeDasharray={`${2 * Math.PI * 38 * apptCompletedPct} ${2 * Math.PI * 38}`}
                        strokeDashoffset="0"
                        transform="rotate(-90 50 50)"
                      />
                    )}
                    {/* Scheduled / Active: Indigo arc */}
                    {apptScheduledPct > 0 && (
                      <circle
                        cx="50"
                        cy="50"
                        r="38"
                        fill="none"
                        stroke="#6366F1"
                        strokeWidth="16"
                        strokeDasharray={`${2 * Math.PI * 38 * apptScheduledPct} ${2 * Math.PI * 38}`}
                        strokeDashoffset={`-${2 * Math.PI * 38 * apptCompletedPct}`}
                        transform="rotate(-90 50 50)"
                      />
                    )}
                    {/* Cancelled: Red arc */}
                    {apptCancelledPct > 0 && (
                      <circle
                        cx="50"
                        cy="50"
                        r="38"
                        fill="none"
                        stroke="#EF4444"
                        strokeWidth="16"
                        strokeDasharray={`${2 * Math.PI * 38 * apptCancelledPct} ${2 * Math.PI * 38}`}
                        strokeDashoffset={`-${2 * Math.PI * 38 * (apptCompletedPct + apptScheduledPct)}`}
                        transform="rotate(-90 50 50)"
                      />
                    )}
                  </>
                )}
              </svg>
              <div className="donut-center-label">
                <span className="donut-big-num">{appointmentsTotal}</span>
                <span className="donut-sub-text">Total</span>
              </div>
            </div>

            <div className="donut-legend">
              <div className="legend-row">
                <div className="legend-label-wrap">
                  <span className="legend-indicator dot-green" />
                  <span className="legend-name">Completed</span>
                </div>
                <span className="legend-val">
                  {appointmentsCompleted} ({appointmentsTotal > 0 ? Math.round(apptCompletedPct * 100) : 0}%)
                </span>
              </div>
              <div className="legend-row">
                <div className="legend-label-wrap">
                  <span className="legend-indicator dot-indigo" />
                  <span className="legend-name">Scheduled</span>
                </div>
                <span className="legend-val">
                  {appointmentsScheduled} ({appointmentsTotal > 0 ? Math.round(apptScheduledPct * 100) : 0}%)
                </span>
              </div>
              <div className="legend-row">
                <div className="legend-label-wrap">
                  <span className="legend-indicator dot-red" />
                  <span className="legend-name">Cancelled</span>
                </div>
                <span className="legend-val">
                  {appointmentsCancelled} ({appointmentsTotal > 0 ? Math.round(apptCancelledPct * 100) : 0}%)
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Donut 2: Billing Overview (All Invoices) */}
        <div className="dashboard-card donut-card">
          <div className="card-header-flex">
            <div>
              <h3 className="card-heading">Billing Overview</h3>
              <p className="card-subheading">Collected vs Due (All Invoices)</p>
            </div>
            <div className="card-header-icon-pill bg-lavender">
              <IndianRupee size={16} className="text-purple" />
            </div>
          </div>

          <div className="donut-body">
            <div className="donut-chart-container">
              <svg viewBox="0 0 100 100" className="donut-svg">
                {/* Clean base track ring */}
                <circle
                  cx="50"
                  cy="50"
                  r="38"
                  fill="none"
                  stroke="#F1F5F9"
                  strokeWidth="16"
                />
                {allRevenueTotal > 0 && (
                  <>
                    {/* Collected: Green */}
                    {billingCollectedPct > 0 && (
                      <circle
                        cx="50"
                        cy="50"
                        r="38"
                        fill="none"
                        stroke="#10B981"
                        strokeWidth="16"
                        strokeDasharray={`${2 * Math.PI * 38 * billingCollectedPct} ${2 * Math.PI * 38}`}
                        strokeDashoffset="0"
                        transform="rotate(-90 50 50)"
                      />
                    )}
                    {/* Due: Orange */}
                    {billingDuePct > 0 && (
                      <circle
                        cx="50"
                        cy="50"
                        r="38"
                        fill="none"
                        stroke="#F59E0B"
                        strokeWidth="16"
                        strokeDasharray={`${2 * Math.PI * 38 * billingDuePct} ${2 * Math.PI * 38}`}
                        strokeDashoffset={`-${2 * Math.PI * 38 * billingCollectedPct}`}
                        transform="rotate(-90 50 50)"
                      />
                    )}
                  </>
                )}
              </svg>
              <div className="donut-center-label">
                <span className="donut-big-num font-small">
                  ₹{allRevenueTotal.toLocaleString("en-IN")}
                </span>
                <span className="donut-sub-text">Total</span>
              </div>
            </div>

            <div className="donut-legend">
              <div className="legend-row">
                <div className="legend-label-wrap">
                  <span className="legend-indicator dot-green" />
                  <span className="legend-name">Collected</span>
                </div>
                <span className="legend-val">
                  ₹{allRevenueCollected.toLocaleString("en-IN")} ({(billingCollectedPct * 100).toFixed(1)}%)
                </span>
              </div>
              <div className="legend-row">
                <div className="legend-label-wrap">
                  <span className="legend-indicator dot-orange" />
                  <span className="legend-name">Due</span>
                </div>
                <span className="legend-val">
                  ₹{allRevenueDue.toLocaleString("en-IN")} ({(billingDuePct * 100).toFixed(1)}%)
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ========================================================
          4. STATUS & VISIT TYPE BADGES ROW (REAL DATABASE DATA)
      ======================================================== */}
      <section className="status-visit-row">
        {/* Left: BY STATUS */}
        <div className="status-group">
          <p className="group-heading">
            BY STATUS · {selectedDate === todayIso ? "TODAY" : selectedDateFormatted.toUpperCase()}
          </p>
          <div className="status-cards-grid">
            <div className="status-tile tile-blue">
              <div className="status-icon-circle blue">
                <Clock size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{waitingCount}</span>
                <span className="status-name">Waiting</span>
              </div>
            </div>

            <div className="status-tile tile-green">
              <div className="status-icon-circle green">
                <CheckCircle2 size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{confirmedCount}</span>
                <span className="status-name">Confirmed</span>
              </div>
            </div>

            <div className="status-tile tile-amber">
              <div className="status-icon-circle amber">
                <Stethoscope size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{inConsultationCount}</span>
                <span className="status-name">In consultation</span>
              </div>
            </div>

            <div className="status-tile tile-purple">
              <div className="status-icon-circle purple">
                <ClipboardCheck size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{completedCount}</span>
                <span className="status-name">Completed</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right: BY VISIT TYPE */}
        <div className="status-group">
          <p className="group-heading">
            BY VISIT TYPE · {selectedDate === todayIso ? "TODAY" : selectedDateFormatted.toUpperCase()}
          </p>
          <div className="status-cards-grid">
            <div className="status-tile tile-green">
              <div className="status-icon-circle green">
                <Building2 size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{inClinicCount}</span>
                <span className="status-name">In clinic</span>
              </div>
            </div>

            <div className="status-tile tile-blue">
              <div className="status-icon-circle blue">
                <Home size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{homeVisitCount}</span>
                <span className="status-name">Home visit</span>
              </div>
            </div>

            <div className="status-tile tile-purple">
              <div className="status-icon-circle purple">
                <Video size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{videoCount}</span>
                <span className="status-name">Video</span>
              </div>
            </div>

            <div className="status-tile tile-red">
              <div className="status-icon-circle red">
                <AlertTriangle size={16} />
              </div>
              <div className="status-data">
                <span className="status-num">{emergencyCount}</span>
                <span className="status-name">Emergency</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ========================================================
          5. BOTTOM 3-COLUMN GRID: UPCOMING, CALENDAR, RECENT ACTIVITY
      ======================================================== */}
      <section className="bottom-three-grid">
        {/* Column 1: Upcoming Appointments */}
        <div className="dashboard-card upcoming-appointments-card">
          <div className="card-header-flex">
            <h3 className="card-heading">Upcoming Appointments</h3>
            <button
              type="button"
              className="card-header-link"
              onClick={() => navigate("/appointments")}
            >
              View all
            </button>
          </div>

          <div className="upcoming-list">
            {upcomingAppointments.length === 0 ? (
              <div className="dashboard-empty-state">
                <Calendar size={28} className="empty-state-icon" />
                <p className="empty-state-title">No upcoming appointments</p>
                <p className="empty-state-sub">
                  No upcoming visits scheduled for today or upcoming days.
                </p>
                <button
                  type="button"
                  className="empty-action-btn"
                  onClick={() => navigate("/appointments")}
                >
                  Schedule Visit
                </button>
              </div>
            ) : (
              upcomingAppointments.map((appt) => {
                const icon = SPECIES_ICONS[appt.species] || "🐾";
                const typeColor =
                  String(appt.appointmentType).toLowerCase() === "emergency"
                    ? "red"
                    : String(appt.appointmentType).toLowerCase() === "vaccination"
                    ? "green"
                    : "blue";

                return (
                  <div key={appt.id} className="upcoming-item">
                    <div className="pet-avatar-fallback">{icon}</div>

                    <div className="upcoming-pet-info">
                      <div className="upcoming-name-row">
                        <span className="pet-name">{appt.patientName || "Patient"}</span>
                      </div>
                      <span className="pet-sub-details">
                        {appt.breed || appt.species || "Pet"} {appt.ownerName ? `· ${appt.ownerName}` : ""}
                      </span>
                    </div>

                    <div className="upcoming-time-tag">
                      <span className="appt-time-text">{appt.appointmentTime || "Scheduled"}</span>
                      <span className={`appt-tag-pill tag-${typeColor}`}>
                        {appt.appointmentType || "Visit"}
                      </span>
                    </div>

                    <div className="upcoming-doc-col">
                      <span className="appt-doc-name">{appt.doctorName || "Veterinarian"}</span>
                      <button
                        type="button"
                        className="appt-options-btn"
                        aria-label="More options"
                        onClick={() => navigate("/appointments")}
                      >
                        <MoreVertical size={15} />
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Column 2: Dynamic Calendar */}
        <div className="dashboard-card calendar-card">
          <div className="card-header-flex">
            <h3 className="card-heading">Calendar · {monthLabel}</h3>
            <div className="calendar-nav-arrows">
              <button
                type="button"
                className="cal-arrow-btn"
                aria-label="Previous month"
                onClick={handlePrevMonth}
              >
                <ChevronLeft size={16} />
              </button>
              <button
                type="button"
                className="cal-arrow-btn"
                aria-label="Next month"
                onClick={handleNextMonth}
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>

          <div className="calendar-widget">
            <div className="calendar-weekdays">
              <span>Sun</span>
              <span>Mon</span>
              <span>Tue</span>
              <span>Wed</span>
              <span>Thu</span>
              <span>Fri</span>
              <span>Sat</span>
            </div>

            <div className="calendar-days-grid">
              {calendarDays.map((cd, index) => {
                const isSelected = cd.iso === selectedDate;
                const isToday = cd.iso === todayIso;
                return (
                  <button
                    key={`${cd.iso}-${index}`}
                    type="button"
                    className={`cal-day-cell ${
                      cd.isOtherMonth ? "day-muted" : ""
                    } ${isSelected ? "day-selected" : ""} ${
                      isToday && !isSelected ? "day-today" : ""
                    }`}
                    onClick={() => {
                      setSelectedDate(cd.iso);
                    }}
                    title={`${isToday ? "Today · " : ""}${cd.iso}`}
                  >
                    <span className="cal-day-number">{cd.day}</span>
                    {cd.hasDot && (
                      <span className="cal-event-dot dot-green" />
                    )}
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        {/* Column 3: Recent Activity (Real Database Activity) */}
        <div className="dashboard-card recent-activity-card">
          <div className="card-header-flex">
            <h3 className="card-heading">Recent Activity</h3>
            <button
              type="button"
              className="card-header-link"
              onClick={() => navigate("/appointments")}
            >
              View all
            </button>
          </div>

          <div className="recent-activity-list">
            {recentActivities.length === 0 ? (
              <div className="dashboard-empty-state">
                <FileText size={28} className="empty-state-icon" />
                <p className="empty-state-title">No recent activity</p>
                <p className="empty-state-sub">
                  Activity will appear as visits and invoices are created.
                </p>
              </div>
            ) : (
              recentActivities.map((act) => {
                const Icon = act.icon;
                return (
                  <div key={act.id} className="activity-item">
                    <div className={`activity-icon-box box-${act.iconBg}`}>
                      <Icon size={16} />
                    </div>

                    <div className="activity-info">
                      <p className="activity-title">{act.title}</p>
                      <p className="activity-sub">{act.sub}</p>
                    </div>

                    <span className="activity-time">{act.time}</span>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
