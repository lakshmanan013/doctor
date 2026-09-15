const VARIANT = {
  Completed: "badge-success",
  Consultation: "badge-info",
  Waiting: "badge-warning",
  Confirmed: "badge-navy",
  Emergency: "badge-danger",
  Cancelled: "badge-slate",
};

export default function StatusBadge({ status }) {
  return (
    <span className={`badge ${VARIANT[status] || "badge-slate"}`}>
      {status}
    </span>
  );
}
