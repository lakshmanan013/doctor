import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Bell,
  CheckCheck,
  CalendarClock,
  AlertTriangle,
  Syringe,
  FileText,
  Clock,
  Check,
  Sparkles,
} from "lucide-react";
import {
  getNotifications,
  getUnreadNotifications,
  markNotificationRead,
  markAllNotificationsRead,
} from "../../services/notificationService";

import "./NotificationBell.css";

const NOTIF_ICONS = {
  followup: {
    Icon: CalendarClock,
    className: "followup",
  },
  stock: {
    Icon: AlertTriangle,
    className: "stock",
  },
  vaccine: {
    Icon: Syringe,
    className: "vaccine",
  },
  appointment: {
    Icon: CalendarClock,
    className: "appointment",
  },
  invoice: {
    Icon: FileText,
    className: "default",
  },
};

function formatTimeAgo(dateString) {
  if (!dateString) return "Recently";
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return "Recently";

  const diffSec = Math.floor((Date.now() - date.getTime()) / 1000);
  if (diffSec < 60) return "Just now";
  const diffMin = Math.floor(diffSec / 60);
  if (diffMin < 60) return `${diffMin}m ago`;
  const diffHours = Math.floor(diffMin / 60);
  if (diffHours < 24) return `${diffHours}h ago`;
  const diffDays = Math.floor(diffHours / 24);
  if (diffDays === 1) return "Yesterday";
  if (diffDays < 7) return `${diffDays}d ago`;
  return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
}

export default function NotificationBell() {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [filter, setFilter] = useState("all"); // "all" | "unread"
  const ref = useRef(null);

  const loadNotifications = async () => {
    try {
      const data = await getNotifications();
      if (Array.isArray(data)) {
        setNotifications(data);
      } else {
        const unread = await getUnreadNotifications();
        setNotifications(Array.isArray(unread) ? unread : []);
      }
    } catch {
      setNotifications([]);
    }
  };

  useEffect(() => {
    loadNotifications();

    // Close on click outside
    const handleClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false);
      }
    };

    // Close on escape
    const handleKeyDown = (e) => {
      if (e.key === "Escape") setOpen(false);
    };

    document.addEventListener("mousedown", handleClickOutside);
    window.addEventListener("keydown", handleKeyDown);
    window.addEventListener("refreshNotifications", loadNotifications);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      window.removeEventListener("keydown", handleKeyDown);
      window.removeEventListener("refreshNotifications", loadNotifications);
    };
  }, []);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const displayedNotifications =
    filter === "unread"
      ? notifications.filter((n) => !n.read)
      : notifications;

  const handleMarkAsRead = async (id, e) => {
    if (e) e.stopPropagation();
    try {
      await markNotificationRead(id);
    } catch {
      // ignore
    }
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  };

  const handleMarkAllAsRead = async () => {
    try {
      await markAllNotificationsRead(1);
    } catch {
      // ignore
    }
    // Optimistic
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const handleNotificationClick = async (n) => {
    if (!n.read) {
      await handleMarkAsRead(n.id);
    }
    setOpen(false);

    // Contextual navigation
    if (n.type === "followup") {
      navigate("/followups");
    } else if (n.type === "stock") {
      navigate("/inventory");
    } else if (n.type === "vaccine") {
      navigate("/vaccinations");
    } else if (n.patientId) {
      navigate("/patients");
    } else {
      navigate("/dashboard");
    }
  };

  return (
    <div className="notif-container" ref={ref}>
      {/* TRIGGER BUTTON */}
      <button
        type="button"
        className={`notif-trigger-btn ${open ? "active" : ""}`}
        aria-label="Notifications"
        onClick={() => {
          setOpen((prev) => !prev);
          if (!open) loadNotifications();
        }}
        title="Notifications"
      >
        <Bell size={18} />
        {unreadCount > 0 && (
          <span className="notif-badge">{unreadCount}</span>
        )}
      </button>

      {/* GLASS DROPDOWN */}
      {open && (
        <div className="notif-dropdown">
          {/* HEADER */}
          <div className="notif-header">
            <div className="notif-header-title">
              <span>Notifications</span>
              {unreadCount > 0 && (
                <span className="notif-unread-pill">{unreadCount} new</span>
              )}
            </div>

            {unreadCount > 0 && (
              <button
                type="button"
                className="notif-mark-all-btn"
                onClick={handleMarkAllAsRead}
                title="Mark all as read"
              >
                <CheckCheck size={14} />
                <span>Mark all read</span>
              </button>
            )}
          </div>

          {/* FILTER TABS */}
          <div className="notif-filter-tabs">
            <button
              type="button"
              className={`notif-tab-btn ${filter === "all" ? "active" : ""}`}
              onClick={() => setFilter("all")}
            >
              All ({notifications.length})
            </button>
            <button
              type="button"
              className={`notif-tab-btn ${filter === "unread" ? "active" : ""}`}
              onClick={() => setFilter("unread")}
            >
              Unread ({unreadCount})
            </button>
          </div>

          {/* NOTIFICATION LIST */}
          <div className="notif-list">
            {displayedNotifications.length > 0 ? (
              displayedNotifications.map((n) => {
                const iconConfig =
                  NOTIF_ICONS[n.type] || NOTIF_ICONS.followup;
                const Icon = iconConfig.Icon;

                return (
                  <div
                    key={n.id}
                    className={`notif-item ${!n.read ? "unread" : ""}`}
                    onClick={() => handleNotificationClick(n)}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(e) => {
                      if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        handleNotificationClick(n);
                      }
                    }}
                  >
                    <div className={`notif-icon-box ${iconConfig.className}`}>
                      <Icon size={18} />
                    </div>

                    <div className="notif-body">
                      <div className="notif-title-row">
                        <span className="notif-item-title">
                          {n.title || "Notification"}
                        </span>
                        <span className="notif-item-time">
                          {formatTimeAgo(n.createdAt)}
                        </span>
                      </div>

                      <p className="notif-item-message">{n.message}</p>
                    </div>

                    {!n.read && <span className="notif-unread-dot" />}
                  </div>
                );
              })
            ) : (
              <div className="notif-empty">
                <div className="notif-empty-icon">
                  <Sparkles size={20} />
                </div>
                <h5>All caught up!</h5>
                <p>No {filter === "unread" ? "unread " : ""}notifications right now.</p>
              </div>
            )}
          </div>

          {/* FOOTER */}
          <div className="notif-footer">
            <button
              type="button"
              className="notif-footer-link"
              onClick={() => {
                setOpen(false);
                navigate("/followups");
              }}
            >
              View Follow-ups &rarr;
            </button>

            <button
              type="button"
              className="notif-footer-link"
              onClick={() => {
                setOpen(false);
                navigate("/inventory");
              }}
            >
              Check Inventory &rarr;
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
