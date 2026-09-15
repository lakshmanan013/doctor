import { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import toast from "react-hot-toast";
import { FiCalendar, FiCheckCircle, FiMail, FiRotateCcw } from "react-icons/fi";
import { FaWhatsapp } from "react-icons/fa";
import { MdNotificationsActive, MdErrorOutline, MdSms } from "react-icons/md";

import { StatCard } from "../../components/ui/Card";
import Badge from "../../components/ui/Badge";
import {
  getFollowups,
  updateFollowup,
  markReminderSent,
} from "../../services/followupService";
import { sendSms, sendWhatsApp, sendEmail } from "../../services/communicationService";
import { openWhatsApp } from "../../utils/whatsapp";
import { toE164 } from "../../utils/phone";
import { getPatientById } from "../../services/patientService";
import { getOwnerById } from "../../services/ownerService";
import "./Followup.css";

const TABS = [
  { key: "dueToday", label: "Due today" },
  { key: "overdue", label: "Overdue" },
  { key: "week", label: "This week" },
  { key: "completed", label: "Completed" },
];

const today = new Date().toISOString().slice(0, 10);

const plusDays = (n) => {
  const d = new Date();
  d.setDate(d.getDate() + n);
  return d.toISOString().slice(0, 10);
};

const isCompleted = (followup) =>
  String(followup.status || "").toUpperCase() === "COMPLETED";

export default function Followup() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const initialTab = TABS.some((t) => t.key === searchParams.get("tab"))
    ? searchParams.get("tab")
    : "dueToday";

  const [tab, setTab] = useState(initialTab);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      setLoading(true);
      const data = await getFollowups();
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      toast.error(e?.response?.data?.message || "Could not load follow-ups");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const changeTab = (key) => {
    setTab(key);
    setSearchParams(key === "dueToday" ? {} : { tab: key });
  };

  const buckets = useMemo(() => {
    const dueToday = [];
    const overdue = [];
    const week = [];
    const completed = [];
    const weekLimit = plusDays(7);

    items.forEach((followup) => {
      if (isCompleted(followup)) {
        completed.push(followup);
        return;
      }

      const status = String(followup.status || "").toUpperCase();
      const date = followup.nextFollowUpDate || followup.followUpDate;

      // If marked PENDING or OVERDUE, or due date is in the past, it goes to Overdue
      if (
        status === "PENDING" ||
        status === "OVERDUE" ||
        (date && date < today)
      ) {
        overdue.push(followup);
        return;
      }

      if (!date || date === today) {
        dueToday.push(followup);
        return;
      }

      if (date <= weekLimit) {
        week.push(followup);
      } else {
        dueToday.push(followup);
      }
    });

    const byDate = (a, b) =>
      String(a.nextFollowUpDate || a.followUpDate || "").localeCompare(
        String(b.nextFollowUpDate || b.followUpDate || "")
      );

    dueToday.sort(byDate);
    overdue.sort(byDate);
    week.sort(byDate);
    completed.sort(byDate);

    return { dueToday, overdue, week, completed };
  }, [items]);

  const counts = {
    dueToday: buckets.dueToday.length,
    overdue: buckets.overdue.length,
    week: buckets.week.length,
    completed: buckets.completed.length,
  };

  const visible = buckets[tab] || [];

  const done = async (f) => {
    try {
      await updateFollowup(f.id, {
        patientId: f.patientId,
        appointmentId: f.appointmentId,
        followUpDate: f.followUpDate,
        nextFollowUpDate: f.nextFollowUpDate,
        reason: f.reason,
        status: "COMPLETED",
        symptoms: f.symptoms,
        findings: f.findings,
        treatment: f.treatment,
        recommendations: f.recommendations,
        notes: f.notes,
        doctorName: f.doctorName,
        reminderSent: f.reminderSent,
      });
      toast.success("Follow-up completed");
      await load();
      changeTab("completed");
    } catch (e) {
      toast.error(e?.response?.data?.message || "Could not update follow-up");
    }
  };

  const markPending = async (f) => {
    try {
      await updateFollowup(f.id, {
        patientId: f.patientId,
        appointmentId: f.appointmentId,
        followUpDate: f.followUpDate,
        nextFollowUpDate: f.nextFollowUpDate,
        reason: f.reason,
        status: "PENDING",
        symptoms: f.symptoms,
        findings: f.findings,
        treatment: f.treatment,
        recommendations: f.recommendations,
        notes: f.notes,
        doctorName: f.doctorName,
        reminderSent: f.reminderSent,
      });
      toast.success("Follow-up marked pending");
      await load();
      changeTab("overdue");
    } catch (e) {
      toast.error(e?.response?.data?.message || "Could not update follow-up");
    }
  };

  const reminder = async (f, type) => {
    let phone = f.ownerPhone || "";
    let email = f.ownerEmail || "";
    const message = `Follow-up for ${f.patientName || "your pet"} on ${
      f.nextFollowUpDate || f.followUpDate || ""
    }.`;

    try {
      if (!phone || !email) {
        const p = await getPatientById(f.patientId).catch(() => null);
        if (p?.ownerPhone) phone = p.ownerPhone;
        if (p?.ownerId) {
          const o = await getOwnerById(p.ownerId).catch(() => null);
          if (o?.email) email = o.email;
        }
      }

      if (!phone && type !== "email") throw new Error("Owner phone is not available");
      if (!email && type === "email") throw new Error("Owner email is not available");

      if (type === "whatsapp") {
        const opened = openWhatsApp(phone, message);
        if (!opened) throw new Error("Owner phone is not available");
        sendWhatsApp({
          phoneNumber: phone,
          message,
          type: "FOLLOWUP",
          provider: "MANUAL",
        }).catch(() => {});
        await markReminderSent(f.id);
        toast.success("WhatsApp send successfully");
        load();
        return;
      }

      if (type === "sms") {
        await sendSms({
          phoneNumber: toE164(phone),
          message,
          type: "FOLLOWUP",
          provider: "MANUAL",
        });
      }

      if (type === "email") {
        await sendEmail({
          recipient: email,
          subject: "Veterinary follow-up reminder",
          message,
          type: "FOLLOWUP",
          provider: "MANUAL",
        });
      }

      await markReminderSent(f.id);
      toast.success(`${type === "sms" ? "SMS" : "Mail"} send successfully`);
      load();
    } catch (e) {
      toast.error(e?.response?.data?.message || `Could not send ${type} reminder`);
    }
  };

  if (loading) {
    return (
      <div className="table-card">
        <div className="table-empty">Loading follow-ups...</div>
      </div>
    );
  }

  return (
    <div className="stack-6">
      <div className="stat-grid">
        <StatCard
          icon={MdNotificationsActive}
          label="Due today"
          value={counts.dueToday}
          iconBg="warning"
          hoverColor="#EAB308"
          onClick={() => changeTab("dueToday")}
        />
        <StatCard
          icon={MdErrorOutline}
          label="Overdue"
          value={counts.overdue}
          iconBg="danger"
          hoverColor="#DC2626"
          onClick={() => changeTab("overdue")}
        />
        <StatCard
          icon={FiCalendar}
          label="This week"
          value={counts.week}
          iconBg="info"
          hoverColor="#2563EB"
          onClick={() => changeTab("week")}
        />
        <StatCard
          icon={FiCheckCircle}
          label="Completed"
          value={counts.completed}
          iconBg="success"
          hoverColor="#16A34A"
          onClick={() => changeTab("completed")}
        />
      </div>

      <div className="filter-row">
        {TABS.map((t) => (
          <button
            key={t.key}
            type="button"
            onClick={() => changeTab(t.key)}
            className={`filter-chip ${tab === t.key ? "active" : ""}`}
          >
            {t.label} ({counts[t.key]})
          </button>
        ))}
      </div>

      <div className="table-card">
        {visible.map((f) => (
          <div
            key={f.id}
            className="fu-row"
            style={{ cursor: "pointer" }}
            onClick={() => navigate(`/patients/${f.patientId}`)}
          >
            <div className="row-avatar" style={{ width: 40, height: 40 }}>
              🐾
            </div>

            <div className="row-body">
              <p className="cell-title">{f.patientName || "Patient"}</p>
              <p className="cell-sub">
                {f.reason || f.notes || "Follow-up"} · {f.status || "Scheduled"}
              </p>
            </div>

            <div style={{ textAlign: "right" }}>
              <p className="fu-date">{f.nextFollowUpDate || f.followUpDate || "—"}</p>
              <p className="cell-sub">{f.ownerName || ""}</p>
            </div>

            <Badge
              variant={
                isCompleted(f)
                  ? "success"
                  : tab === "overdue" ||
                    String(f.status || "").toUpperCase() === "PENDING" ||
                    String(f.status || "").toUpperCase() === "OVERDUE" ||
                    (f.nextFollowUpDate && f.nextFollowUpDate < today) ||
                    (f.followUpDate && f.followUpDate < today)
                  ? "danger"
                  : "slate"
              }
            >
              {isCompleted(f)
                ? "Completed"
                : tab === "overdue" ||
                  String(f.status || "").toUpperCase() === "PENDING" ||
                  String(f.status || "").toUpperCase() === "OVERDUE" ||
                  (f.nextFollowUpDate && f.nextFollowUpDate < today) ||
                  (f.followUpDate && f.followUpDate < today)
                ? "Overdue"
                : f.reminderSent
                ? "Reminder sent"
                : "Scheduled"}
            </Badge>

            {tab === "completed" ? (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  markPending(f);
                }}
                className="fu-action"
              >
                <FiRotateCcw size={15} /> Mark pending
              </button>
            ) : (
              <>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    reminder(f, "whatsapp");
                  }}
                  className="fu-action fu-whatsapp"
                >
                  <FaWhatsapp size={14} /> WhatsApp
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    reminder(f, "sms");
                  }}
                  className="fu-icon-btn"
                  title="SMS"
                >
                  <MdSms size={16} />
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    reminder(f, "email");
                  }}
                  className="fu-icon-btn"
                  title="Email"
                >
                  <FiMail size={16} />
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    done(f);
                  }}
                  className="fu-action"
                >
                  <FiCheckCircle size={15} /> Done
                </button>
              </>
            )}
          </div>
        ))}

        {!visible.length && (
          <p className="table-empty">
            {tab === "dueToday"
              ? "No follow-ups due today."
              : tab === "overdue"
              ? "No overdue follow-ups."
              : tab === "week"
              ? "No follow-ups due this week."
              : "No completed follow-ups."}
          </p>
        )}
      </div>
    </div>
  );
}
