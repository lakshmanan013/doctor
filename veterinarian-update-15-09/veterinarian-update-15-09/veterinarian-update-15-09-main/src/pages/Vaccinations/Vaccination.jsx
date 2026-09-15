import { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import toast from "react-hot-toast";
import { FiClock, FiCheckCircle, FiCalendar } from "react-icons/fi";
import { MdErrorOutline } from "react-icons/md";

import { StatCard } from "../../components/ui/Card";
import Badge from "../../components/ui/Badge";
import {
  getVaccinations,
  updateVaccination,
} from "../../services/vaccinationService";
import "./Vaccination.css";

const TABS = [
  { key: "dueNow", label: "Due now" },
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

const isCompleted = (vaccination) =>
  String(vaccination.status || "").toUpperCase() === "COMPLETED";

export default function Vaccination() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const initialTab = TABS.some((t) => t.key === searchParams.get("tab"))
    ? searchParams.get("tab")
    : "dueNow";

  const [tab, setTab] = useState(initialTab);
  const [vaccinations, setVaccinations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);

  const load = async () => {
    try {
      setLoading(true);
      const data = await getVaccinations();
      setVaccinations(Array.isArray(data) ? data : []);
    } catch (error) {
      toast.error(
        error?.response?.data?.message || "Could not load vaccinations"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const changeTab = (key) => {
    setTab(key);
    setSearchParams(key === "dueNow" ? {} : { tab: key });
  };

  const buckets = useMemo(() => {
    const overdue = [];
    const dueNow = [];
    const week = [];
    const completed = [];
    const weekLimit = plusDays(7);

    vaccinations.forEach((vaccination) => {
      if (isCompleted(vaccination)) {
        completed.push(vaccination);
        return;
      }

      const status = String(vaccination.status || "").toUpperCase();

      // If marked PENDING or OVERDUE, or due date is in the past, it goes to Overdue
      if (
        status === "PENDING" ||
        status === "OVERDUE" ||
        (vaccination.nextDueDate && vaccination.nextDueDate < today)
      ) {
        overdue.push(vaccination);
        return;
      }

      if (!vaccination.nextDueDate) {
        dueNow.push(vaccination);
        return;
      }

      if (vaccination.nextDueDate === today) {
        dueNow.push(vaccination);
      } else if (vaccination.nextDueDate <= weekLimit) {
        week.push(vaccination);
      } else {
        dueNow.push(vaccination);
      }
    });

    const byDate = (a, b) =>
      String(a.nextDueDate || "").localeCompare(String(b.nextDueDate || ""));

    overdue.sort(byDate);
    dueNow.sort(byDate);
    week.sort(byDate);
    completed.sort(byDate);

    return { overdue, dueNow, week, completed };
  }, [vaccinations]);

  const counts = {
    dueNow: buckets.dueNow.length,
    overdue: buckets.overdue.length,
    week: buckets.week.length,
    completed: buckets.completed.length,
  };

  const visible = buckets[tab] || [];

  const handleToggleStatus = async (vaccination) => {
    const completed = isCompleted(vaccination);
    const nextStatus = completed ? "PENDING" : "COMPLETED";

    setUpdatingId(vaccination.id);

    const previous = vaccinations;
    setVaccinations((current) =>
      current.map((v) =>
        v.id === vaccination.id ? { ...v, status: nextStatus } : v
      )
    );

    try {
      await updateVaccination(vaccination.id, { status: nextStatus });
      toast.success(
        nextStatus === "COMPLETED"
          ? "Vaccination marked completed"
          : "Vaccination marked pending"
      );
      changeTab(nextStatus === "COMPLETED" ? "completed" : "overdue");
    } catch (error) {
      setVaccinations(previous);
      toast.error(
        error?.response?.data?.message || "Could not update vaccination"
      );
    } finally {
      setUpdatingId(null);
    }
  };

  if (loading) {
    return (
      <div className="table-card">
        <div className="table-empty">Loading vaccinations...</div>
      </div>
    );
  }

  return (
    <div className="stack-6">
      <div className="stat-grid">
        <StatCard
          icon={FiClock}
          label="Due now"
          value={counts.dueNow}
          iconBg="info"
          hoverColor="#2563EB"
          onClick={() => changeTab("dueNow")}
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
          iconBg="primary"
          hoverColor="#0F4C5C"
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
        <div className="row-list">
          {visible.map((vaccination) => {
            const completed = isCompleted(vaccination);
            const status = String(vaccination.status || "").toUpperCase();
            const isOverdue =
              !completed &&
              (tab === "overdue" ||
                status === "PENDING" ||
                status === "OVERDUE" ||
                (vaccination.nextDueDate && vaccination.nextDueDate < today));
            const dueThisWeek = !completed && !isOverdue && tab === "week";
            const isUpdating = updatingId === vaccination.id;

            return (
              <div key={vaccination.id} className="row-item">
                <div
                  className="row-avatar"
                  style={{ cursor: "pointer" }}
                  onClick={() =>
                    navigate(`/patients/${vaccination.patientId}`)
                  }
                >
                  🐾
                </div>

                <div
                  className="row-body"
                  style={{ cursor: "pointer" }}
                  onClick={() =>
                    navigate(`/patients/${vaccination.patientId}`)
                  }
                >
                  <p className="row-title">
                    {vaccination.patientName || "Patient"}
                    {vaccination.vaccineName
                      ? ` · ${vaccination.vaccineName}`
                      : ""}
                  </p>
                  <p className="row-desc">
                    {completed
                      ? `Vaccinated ${vaccination.vaccinationDate || "—"}`
                      : `Due ${vaccination.nextDueDate || "—"}`}
                  </p>
                </div>

                <div className="vaccination-row-actions">
                  <Badge
                    variant={
                      completed
                        ? "success"
                        : isOverdue
                        ? "danger"
                        : dueThisWeek
                        ? "info"
                        : "warning"
                    }
                  >
                    {completed
                      ? "Completed"
                      : isOverdue
                      ? "Overdue"
                      : dueThisWeek
                      ? "This week"
                      : "Due now"}
                  </Badge>

                  <button
                    type="button"
                    className="btn btn-ghost btn-sm"
                    disabled={isUpdating}
                    onClick={(e) => {
                      e.stopPropagation();
                      handleToggleStatus(vaccination);
                    }}
                  >
                    {completed ? "Mark pending" : "Mark completed"}
                  </button>
                </div>
              </div>
            );
          })}

          {!visible.length && (
            <p className="table-empty">
              {tab === "overdue"
                ? "No overdue vaccinations."
                : tab === "week"
                ? "No vaccinations due this week."
                : tab === "completed"
                ? "No completed vaccinations."
                : "No vaccinations due."}
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
